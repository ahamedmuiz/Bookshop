package lk.ijse.finalproject.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.dto.tm.AttendanceHistoryTm;
import lk.ijse.finalproject.dto.tm.AttendanceTm;
import lk.ijse.finalproject.model.AttendanceModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AttendanceController {

    @FXML
    private Button btnClockIn;

    @FXML
    private Button btnClockOut;

    @FXML
    private Button btnReset;

    @FXML
    private ComboBox<String> cmbEmployee;

    @FXML
    private DatePicker dpDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblTotalHours;

    @FXML
    private TableView<AttendanceTm> tblAttendance;

    @FXML
    private TableColumn<?, ?> colTime;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TableView<AttendanceHistoryTm> tblAttendanceHistory;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colFirstIn;

    @FXML
    private TableColumn<?, ?> colLastOut;

    @FXML
    private TableColumn<?, ?> colTotalHoursWorked;

    private ObservableList<AttendanceTm> obList = FXCollections.observableArrayList();
    private ObservableList<AttendanceHistoryTm> historyObList = FXCollections.observableArrayList();

    private int currentEmployeeId = -1;
    private String currentAttendanceId = null;

    public void initialize() {
        setCellValueFactory();
        loadEmployees();
        setDatePickers();
        resetClockButtons();
        setupEmployeeSelectionListener();
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
            List<EmployeeDto> employeeList = AttendanceModel.getAllEmployees();
            ObservableList<String> obList = FXCollections.observableArrayList();

            for (EmployeeDto dto : employeeList) {
                obList.add(dto.getE_ID() + " - " + dto.getE_Name());
            }
            cmbEmployee.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load employees: " + e.getMessage()).show();
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

    private void checkEmployeeClockStatus() {
        String selectedEmployee = cmbEmployee.getValue();
        if (selectedEmployee == null) {
            resetClockButtons();
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate today = LocalDate.now();

            AttendanceDto lastAttendance = AttendanceModel.getLastAttendance(empId);

            if (lastAttendance != null && lastAttendance.getDate().equals(today)) {
                if (lastAttendance.getTimeOut() == null) {
                    // Employee is currently clocked in
                    currentEmployeeId = empId;
                    currentAttendanceId = lastAttendance.getAttId();
                    btnClockIn.setDisable(true);
                    btnClockOut.setDisable(false);
                    lblStatus.setText("Currently clocked in since " + lastAttendance.getTimeIn());
                    loadTodayAttendance(empId, today);
                    return;
                }
            }

            // Employee is not clocked in
            resetClockButtons();
            lblStatus.setText("Ready");
            loadTodayAttendance(empId, today);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error checking attendance status: " + e.getMessage()).show();
            resetClockButtons();
        }
    }

    private void resetClockButtons() {
        btnClockIn.setDisable(false);
        btnClockOut.setDisable(true);
        currentEmployeeId = -1;
        currentAttendanceId = null;
        lblStatus.setText("Ready");
        lblTotalHours.setText("0 hours");
        obList.clear();
    }

    @FXML
    void onClockInClicked(ActionEvent event) {
        String selectedEmployee = cmbEmployee.getValue();
        if (selectedEmployee == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an employee first").show();
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate date = dpDate.getValue();
            LocalTime timeIn = LocalTime.now();

            // Check if already clocked in today
            AttendanceDto lastAttendance = AttendanceModel.getLastAttendance(empId);
            if (lastAttendance != null && lastAttendance.getDate().equals(date) && lastAttendance.getTimeOut() == null) {
                new Alert(Alert.AlertType.ERROR, "This employee is already clocked in today").show();
                return;
            }

            String attId = AttendanceModel.generateNextAttendanceId();
            AttendanceDto dto = new AttendanceDto(attId, empId, null, date, timeIn, null, 0, "Present");

            boolean isSaved = AttendanceModel.saveAttendance(dto);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Clock In Successful at " + timeIn).show();
                lblStatus.setText("Clocked In at " + timeIn);
                currentEmployeeId = empId;
                currentAttendanceId = attId;
                btnClockIn.setDisable(true);
                btnClockOut.setDisable(false);
                loadTodayAttendance(empId, date);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Clock In Failed: " + e.getMessage()).show();
        }
    }

    @FXML
    void onClockOutClicked(ActionEvent event) {
        if (currentEmployeeId == -1 || currentAttendanceId == null) {
            new Alert(Alert.AlertType.ERROR, "No active clock-in session found").show();
            return;
        }

        try {
            LocalTime timeOut = LocalTime.now();
            AttendanceDto lastAttendance = AttendanceModel.getAttendanceById(currentAttendanceId);

            if (lastAttendance == null || lastAttendance.getTimeOut() != null) {
                new Alert(Alert.AlertType.ERROR, "No active clock-in session found").show();
                resetClockButtons();
                return;
            }

            LocalTime timeIn = lastAttendance.getTimeIn();
            long minutesWorked = ChronoUnit.MINUTES.between(timeIn, timeOut);
            double hoursWorked = minutesWorked / 60.0;

            lastAttendance.setTimeOut(timeOut);
            lastAttendance.setHoursWorked(hoursWorked);
            lastAttendance.setStatus("Completed");

            boolean isUpdated = AttendanceModel.updateClockOut(lastAttendance);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Clock Out Successful at " + timeOut).show();
                lblStatus.setText("Clocked Out at " + timeOut);
                lblTotalHours.setText(String.format("%.2f hours", hoursWorked));
                resetClockButtons();
                loadTodayAttendance(currentEmployeeId, lastAttendance.getDate());
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Clock Out Failed: " + e.getMessage()).show();
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
            new Alert(Alert.AlertType.ERROR, "Please select an employee first").show();
            return;
        }

        try {
            int empId = Integer.parseInt(selectedEmployee.split(" - ")[0]);
            LocalDate startDate = dpStartDate.getValue();
            LocalDate endDate = dpEndDate.getValue();

            if (startDate == null || endDate == null) {
                new Alert(Alert.AlertType.ERROR, "Please select both start and end dates").show();
                return;
            }

            if (startDate.isAfter(endDate)) {
                new Alert(Alert.AlertType.ERROR, "Start date must be before end date").show();
                return;
            }

            List<AttendanceDto> history = AttendanceModel.getAttendanceHistory(empId, startDate, endDate);

            if (history.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No attendance records found for selected period").show();
                historyObList.clear();
            } else {
                loadAttendanceHistory(history);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Search Failed: " + e.getMessage()).show();
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

                    // Calculate and display total hours for completed sessions
                    if (dto.getDate().equals(LocalDate.now())) {
                        lblTotalHours.setText(String.format("%.2f hours", dto.getHoursWorked()));
                    }
                }
            }

            tblAttendance.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load today's attendance: " + e.getMessage()).show();
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
}