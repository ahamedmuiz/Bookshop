package lk.ijse.finalproject.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.dto.tm.AttendanceHistoryTm;
import lk.ijse.finalproject.dto.tm.AttendanceTm;
import lk.ijse.finalproject.model.AttendanceModel;
import lk.ijse.finalproject.model.EmployeeModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AttendanceController {

    @FXML private Button btnClockIn;
    @FXML private Button btnClockOut;
    @FXML private Button btnReset;
    @FXML private ComboBox<String> cmbEmployee;
    @FXML private DatePicker dpDate;
    @FXML private DatePicker dpEndDate;
    @FXML private DatePicker dpStartDate;
    @FXML private Label lblStatus;
    @FXML private Label lblTotalHours;
    @FXML private TableView<AttendanceTm> tblAttendance;
    @FXML private TableColumn<?, ?> colTime;
    @FXML private TableColumn<?, ?> colType;
    @FXML private TableView<AttendanceHistoryTm> tblAttendanceHistory;
    @FXML private TableColumn<?, ?> colDate;
    @FXML private TableColumn<?, ?> colFirstIn;
    @FXML private TableColumn<?, ?> colLastOut;
    @FXML private TableColumn<?, ?> colTotalHoursWorked;

    private ObservableList<AttendanceTm> obList = FXCollections.observableArrayList();
    private ObservableList<AttendanceHistoryTm> historyObList = FXCollections.observableArrayList();
    private Timeline hoursUpdateTimer;
    private double currentSessionHours = 0.0;
    private int currentEmployeeId = -1;
    private String currentAttendanceId = null;

    public void initialize() {
        initializeTimer();
        setCellValueFactory();
        loadEmployees();
        setDatePickers();
        resetClockButtons();
        setupEmployeeSelectionListener();
        setupDateChangeListener();
    }

    private void initializeTimer() {
        hoursUpdateTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    updateCurrentSessionHours();
                    updateTotalHoursDisplay();
                })
        );
        hoursUpdateTimer.setCycleCount(Animation.INDEFINITE);
    }

    private void setCellValueFactory() {
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colFirstIn.setCellValueFactory(new PropertyValueFactory<>("firstIn"));
        colLastOut.setCellValueFactory(new PropertyValueFactory<>("lastOut"));
        colTotalHoursWorked.setCellValueFactory(new PropertyValueFactory<>("totalHours"));
    }

    private void loadEmployees() {
        try {
            List<EmployeeDto> employeeList = EmployeeModel.getAllEmployees();
            ObservableList<String> obList = FXCollections.observableArrayList();
            for (EmployeeDto dto : employeeList) {
                obList.add(dto.getE_ID() + " - " + dto.getE_Name());
            }
            cmbEmployee.setItems(obList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Failed to load employees: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setDatePickers() {
        dpDate.setValue(LocalDate.now());
        dpStartDate.setValue(LocalDate.now().minusDays(7));
        dpEndDate.setValue(LocalDate.now());
    }

    private void setupEmployeeSelectionListener() {
        cmbEmployee.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                checkEmployeeClockStatus();
            } else {
                resetClockButtons();
            }
        });
    }

    private void setupDateChangeListener() {
        dpDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && currentEmployeeId != -1) {
                checkEmployeeClockStatus();
            }
        });
    }

    private void checkEmployeeClockStatus() {
        String selectedEmployee = cmbEmployee.getValue();
        if (selectedEmployee == null) {
            resetClockButtons();
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate selectedDate = dpDate.getValue();
            currentEmployeeId = empId;

            AttendanceDto lastAttendance = AttendanceModel.getLastAttendance(empId);

            if (lastAttendance != null && lastAttendance.getDate().equals(selectedDate) &&
                    lastAttendance.getTimeOut() == null) {

                currentAttendanceId = lastAttendance.getAttId();
                btnClockIn.setDisable(true);
                btnClockOut.setDisable(false);
                lblStatus.setText("Currently clocked in since " + lastAttendance.getTimeIn());
                hoursUpdateTimer.play();
            } else {

                btnClockIn.setDisable(false);
                btnClockOut.setDisable(true);
                lblStatus.setText("Ready");
                currentAttendanceId = null;
                hoursUpdateTimer.stop();
            }

            loadTodayAttendance(empId, selectedDate);
            updateTotalHoursDisplay();

        } catch (Exception e) {
            showAlert("Error checking attendance status: " + e.getMessage(), Alert.AlertType.ERROR);
            resetClockButtons();
        }
    }

    private void updateCurrentSessionHours() {
        if (currentAttendanceId != null) {
            try {
                AttendanceDto currentAttendance = AttendanceModel.getAttendanceById(currentAttendanceId);
                if (currentAttendance != null && currentAttendance.getTimeOut() == null) {
                    LocalTime timeIn = currentAttendance.getTimeIn();
                    LocalTime now = LocalTime.now();
                    long minutesWorked = ChronoUnit.MINUTES.between(timeIn, now);
                    currentSessionHours = minutesWorked / 60.0;
                }
            } catch (SQLException e) {
                showAlert("Error updating hours: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void updateTotalHoursDisplay() {
        try {
            if (currentEmployeeId != -1 && dpDate.getValue() != null) {
                double totalHours = AttendanceModel.getTotalDailyHours(currentEmployeeId, dpDate.getValue());

                if (currentAttendanceId != null) {
                    totalHours += currentSessionHours;
                }

                lblTotalHours.setText(String.format("%.2f hours", totalHours));
            } else {
                lblTotalHours.setText("0.00 hours");
            }
        } catch (SQLException e) {
            showAlert("Error calculating total hours: " + e.getMessage(), Alert.AlertType.ERROR);
            lblTotalHours.setText("0.00 hours");
        }
    }

    private void loadTodayAttendance(int empId, LocalDate date) {
        obList.clear();
        try {
            List<AttendanceDto> todayAttendance = AttendanceModel.getAttendanceHistory(empId, date, date);
            for (AttendanceDto dto : todayAttendance) {
                obList.add(new AttendanceTm(
                        new SimpleStringProperty(dto.getTimeIn().toString()),
                        new SimpleStringProperty("Clock In")
                ));
                if (dto.getTimeOut() != null) {
                    obList.add(new AttendanceTm(
                            new SimpleStringProperty(dto.getTimeOut().toString()),
                            new SimpleStringProperty("Clock Out")
                    ));
                }
            }
            tblAttendance.setItems(obList);
        } catch (SQLException e) {
            showAlert("Failed to load today's attendance: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onClockInClicked(ActionEvent event) {
        String selectedEmployee = cmbEmployee.getValue();
        if (selectedEmployee == null) {
            showAlert("Please select an employee first", Alert.AlertType.ERROR);
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate date = dpDate.getValue();
            LocalTime timeIn = LocalTime.now();

            String attId = AttendanceModel.generateNextAttendanceId();
            AttendanceDto dto = new AttendanceDto(
                    attId, empId, null, date, timeIn, null, 0.0, "Present", 0
            );

            if (AttendanceModel.saveAttendance(dto)) {
                showAlert("Clock In Successful at " + timeIn, Alert.AlertType.INFORMATION);
                lblStatus.setText("Clocked In at " + timeIn);
                currentEmployeeId = empId;
                currentAttendanceId = attId;
                btnClockIn.setDisable(true);
                btnClockOut.setDisable(false);
                hoursUpdateTimer.play();
                loadTodayAttendance(empId, date);
            }
        } catch (Exception e) {
            showAlert("Clock In Failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onClockOutClicked(ActionEvent event) {
        if (currentEmployeeId == -1 || currentAttendanceId == null) {
            showAlert("No active clock-in session found", Alert.AlertType.ERROR);
            return;
        }

        try {
            hoursUpdateTimer.stop();
            LocalTime timeOut = LocalTime.now();
            AttendanceDto lastAttendance = AttendanceModel.getAttendanceById(currentAttendanceId);

            if (lastAttendance == null || lastAttendance.getTimeOut() != null) {
                showAlert("No active clock-in session found", Alert.AlertType.ERROR);
                resetClockButtons();
                return;
            }

            LocalTime timeIn = lastAttendance.getTimeIn();
            long minutesWorked = ChronoUnit.MINUTES.between(timeIn, timeOut);
            double hoursWorked = minutesWorked / 60.0;

            lastAttendance.setTimeOut(timeOut);
            lastAttendance.setHoursWorked(hoursWorked);
            lastAttendance.setStatus("Completed");

            if (AttendanceModel.updateClockOut(lastAttendance)) {
                showAlert("Clock Out Successful at " + timeOut, Alert.AlertType.INFORMATION);
                lblStatus.setText("Clocked Out at " + timeOut);
                currentSessionHours = 0.0;
                loadTodayAttendance(currentEmployeeId, dpDate.getValue());
            }
        } catch (Exception e) {
            showAlert("Clock Out Failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onResetClicked(ActionEvent event) {
        resetClockButtons();
        cmbEmployee.getSelectionModel().clearSelection();
        dpDate.setValue(LocalDate.now());
        obList.clear();
        historyObList.clear();
    }

    @FXML
    void onSearchClicked(ActionEvent event) {
        String selectedEmployee = cmbEmployee.getValue();
        if (selectedEmployee == null) {
            showAlert("Please select an employee first", Alert.AlertType.ERROR);
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate startDate = dpStartDate.getValue();
            LocalDate endDate = dpEndDate.getValue();

            if (startDate == null || endDate == null) {
                showAlert("Please select both start and end dates", Alert.AlertType.ERROR);
                return;
            }

            if (startDate.isAfter(endDate)) {
                showAlert("Start date must be before end date", Alert.AlertType.ERROR);
                return;
            }

            List<AttendanceDto> history = AttendanceModel.getAttendanceHistory(empId, startDate, endDate);
            if (history.isEmpty()) {
                showAlert("No attendance records found for selected period", Alert.AlertType.INFORMATION);
                historyObList.clear();
            } else {
                loadAttendanceHistory(history);
            }
        } catch (Exception e) {
            showAlert("Search Failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAttendanceHistory(List<AttendanceDto> history) {
        historyObList.clear();
        for (AttendanceDto dto : history) {
            String firstIn = dto.getTimeIn() != null ? dto.getTimeIn().toString() : "-";
            String lastOut = dto.getTimeOut() != null ? dto.getTimeOut().toString() : "-";
            String totalHours = dto.getHoursWorked() > 0 ? String.format("%.2f", dto.getHoursWorked()) : "-";

            historyObList.add(new AttendanceHistoryTm(
                    new SimpleStringProperty(dto.getDate().toString()),
                    new SimpleStringProperty(firstIn),
                    new SimpleStringProperty(lastOut),
                    new SimpleStringProperty(totalHours)
            ));
        }
        tblAttendanceHistory.setItems(historyObList);
    }

    private void resetClockButtons() {
        btnClockIn.setDisable(false);
        btnClockOut.setDisable(true);
        currentEmployeeId = -1;
        currentAttendanceId = null;
        lblStatus.setText("Ready");
        lblTotalHours.setText("0.00 hours");
        obList.clear();
        hoursUpdateTimer.stop();
        currentSessionHours = 0.0;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        new Alert(alertType, message).show();
    }
}