package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.dto.SalaryDto;
import lk.ijse.finalproject.dto.tm.SalaryTm;
import lk.ijse.finalproject.model.AttendanceModel;
import lk.ijse.finalproject.model.EmployeeModel;
import lk.ijse.finalproject.model.SalaryModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalaryController {

    @FXML private Button btnCalculate;
    @FXML private Button btnClear;
    @FXML private Button btnLoad;
    @FXML private Button btnPaySalary;

    @FXML private ComboBox<String> cmbEmployee;
    @FXML private ComboBox<String> cmbMonth;

    @FXML private TableColumn<SalaryTm, Double> colDailyWage;
    @FXML private TableColumn<SalaryTm, LocalDate> colDate;
    @FXML private TableColumn<SalaryTm, String> colDay;
    @FXML private TableColumn<SalaryTm, Double> colHoursWorked;
    @FXML private TableColumn<SalaryTm, String> colStatus;

    @FXML private Label lblEmployeeId;
    @FXML private Label lblHourlyRate;
    @FXML private Label lblMonthlySalary;
    @FXML private Label lblPaymentDate;
    @FXML private Label lblTotalHours;
    @FXML private Label lblWorkingDays;

    @FXML private TableView<SalaryTm> tblDailyWages;
    @FXML private TextField txtYear;

    private ObservableList<SalaryTm> obList = FXCollections.observableArrayList();
    private int selectedEmployeeId = -1;
    private double currentHourlyRate = 0;

    public void initialize() {
        setCellValueFactory();
        loadMonths();
        loadEmployeeIds();
        setDefaultMonthAndYear();
    }

    private void setCellValueFactory() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colHoursWorked.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        colDailyWage.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadMonths() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        cmbMonth.getItems().addAll(months);
        cmbMonth.getSelectionModel().select(LocalDate.now().getMonth().toString());
    }

    private void setDefaultMonthAndYear() {
        // Set current month and year by default
        cmbMonth.getSelectionModel().select(LocalDate.now().getMonth().toString());
        txtYear.setText(String.valueOf(LocalDate.now().getYear()));
    }

    private void loadEmployeeIds() {
        try {
            List<EmployeeDto> employeeList = EmployeeModel.getAllEmployees();
            for (EmployeeDto dto : employeeList) {
                cmbEmployee.getItems().add(dto.getE_ID() + " - " + dto.getE_Name());
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Failed to load employees", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void calculateSalary(ActionEvent event) {
        if (validateInputs()) {
            try {
                String month = cmbMonth.getValue();
                int year = Integer.parseInt(txtYear.getText());

                YearMonth yearMonth = YearMonth.of(year, getMonthNumber(month));
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                // Get working days and total hours using the new methods
                int workingDays = AttendanceModel.getWorkingDaysCount(selectedEmployeeId, startDate, endDate);
                double totalHours = AttendanceModel.getTotalHoursWorked(selectedEmployeeId, startDate, endDate);

                // Get detailed attendance for the table
                List<AttendanceDto> attendanceList = SalaryModel.getAttendanceForMonth(selectedEmployeeId, month, year);

                obList.clear();
                for (AttendanceDto dto : attendanceList) {
                    obList.add(new SalaryTm(
                            dto.getDate(),
                            dto.getDate().getDayOfWeek().toString(),
                            dto.getHoursWorked(),
                            dto.getHoursWorked() * dto.getHourlyRate(),
                            dto.getStatus(),
                            dto.getHourlyRate()
                    ));
                }

                tblDailyWages.setItems(obList);
                lblWorkingDays.setText(String.valueOf(workingDays));
                lblTotalHours.setText(String.format("%.2f", totalHours));
                lblMonthlySalary.setText(String.format("$%.2f", totalHours * currentHourlyRate));

            } catch (SQLException e) {
                showAlert("Failed to calculate salary: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void clearFields(ActionEvent event) {
        cmbEmployee.getSelectionModel().clearSelection();
        setDefaultMonthAndYear();
        lblEmployeeId.setText("");
        lblHourlyRate.setText("");
        lblWorkingDays.setText("");
        lblTotalHours.setText("");
        lblMonthlySalary.setText("");
        obList.clear();
        selectedEmployeeId = -1;
        currentHourlyRate = 0;
    }

    @FXML
    void loadSalaryData(ActionEvent event) {
        if (validateInputs()) {
            try {
                String selectedEmployee = cmbEmployee.getValue();
                selectedEmployeeId = Integer.parseInt(selectedEmployee.split(" - ")[0]);

                EmployeeDto employee = EmployeeModel.searchEmployee(String.valueOf(selectedEmployeeId));
                if (employee != null) {
                    lblEmployeeId.setText(String.valueOf(employee.getE_ID()));
                    lblHourlyRate.setText(String.format("$%.2f", employee.getHourly_rate()));
                    currentHourlyRate = employee.getHourly_rate();
                }

                // Check if salary already paid for this month
                String month = cmbMonth.getValue();
                int year = Integer.parseInt(txtYear.getText());
                if (SalaryModel.isSalaryPaid(selectedEmployeeId, month, year)) {
                    showAlert("Salary already paid for this employee in " + month + " " + year, Alert.AlertType.WARNING);
                }

            } catch (SQLException | ClassNotFoundException e) {
                showAlert("Failed to load employee data: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void paySalary(ActionEvent event) {
        if (validateInputs() && !obList.isEmpty()) {
            try {
                String month = cmbMonth.getValue();
                int year = Integer.parseInt(txtYear.getText());
                double totalSalary = Double.parseDouble(lblMonthlySalary.getText().replace("$", ""));

                String salaryId = SalaryModel.generateNextSalaryId();
                LocalDate paymentDate = LocalDate.now(); // Pay on current date

                SalaryDto salaryDto = new SalaryDto(
                        salaryId,
                        selectedEmployeeId,
                        month,
                        year,
                        totalSalary,
                        paymentDate,
                        currentHourlyRate
                );

                if (SalaryModel.saveSalary(salaryDto)) {
                    showAlert("Salary paid successfully!", Alert.AlertType.INFORMATION);
                    clearFields(null);
                } else {
                    showAlert("Failed to pay salary", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                showAlert("Failed to process salary payment: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Please calculate salary first", Alert.AlertType.WARNING);
        }
    }

    private boolean validateInputs() {
        if (cmbEmployee.getSelectionModel().isEmpty()) {
            showAlert("Please select an employee", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbMonth.getSelectionModel().isEmpty()) {
            showAlert("Please select a month", Alert.AlertType.WARNING);
            return false;
        }
        if (txtYear.getText().isEmpty() || !txtYear.getText().matches("\\d{4}")) {
            showAlert("Please enter a valid year (YYYY)", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private int getMonthNumber(String monthName) {
        return switch (monthName.toLowerCase()) {
            case "january" -> 1;
            case "february" -> 2;
            case "march" -> 3;
            case "april" -> 4;
            case "may" -> 5;
            case "june" -> 6;
            case "july" -> 7;
            case "august" -> 8;
            case "september" -> 9;
            case "october" -> 10;
            case "november" -> 11;
            case "december" -> 12;
            default -> 0;
        };
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        new Alert(alertType, message).show();
    }
}