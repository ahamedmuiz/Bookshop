package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.model.EmployeeModel;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {

    @FXML
    private TableView<EmployeeDto> tblEmployee;

    @FXML
    private TableColumn<EmployeeDto, Integer> colEmployeeId;

    @FXML
    private TableColumn<EmployeeDto, String> colEmployeeName;

    @FXML
    private TableColumn<EmployeeDto, String> colEmployeeEmail;

    @FXML
    private TableColumn<EmployeeDto, String> colEmployeePhone;

    @FXML
    private TableColumn<EmployeeDto, Double> colHourlyRate;

    @FXML
    private TextField txtEmployeeId;

    @FXML
    private TextField txtEmployeeName;

    @FXML
    private TextField txtEmployeeEmail;

    @FXML
    private TextField txtEmployeePhone;

    @FXML
    private TextField txtHourlyRate;

    private final ObservableList<EmployeeDto> employeeList = FXCollections.observableArrayList();

    public void initialize() {
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("E_ID"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("E_Name"));
        colEmployeeEmail.setCellValueFactory(new PropertyValueFactory<>("E_Email"));
        colEmployeePhone.setCellValueFactory(new PropertyValueFactory<>("E_Contact"));
        colHourlyRate.setCellValueFactory(new PropertyValueFactory<>("hourly_rate"));

        // Add listener to table selection
        tblEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtEmployeeId.setText(String.valueOf(newSelection.getE_ID()));
                txtEmployeeName.setText(newSelection.getE_Name());
                txtEmployeeEmail.setText(newSelection.getE_Email());
                txtEmployeePhone.setText(newSelection.getE_Contact());
                txtHourlyRate.setText(String.valueOf(newSelection.getHourly_rate()));
            }
        });
    }

    @FXML
    void btnEmployeeAdd(ActionEvent event) {
        try {
            if (txtHourlyRate.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Hourly rate is required").show();
                return;
            }

            EmployeeDto employee = new EmployeeDto(
                    Integer.parseInt(txtEmployeeId.getText()),
                    txtEmployeeName.getText(),
                    txtEmployeeEmail.getText(),
                    txtEmployeePhone.getText(),
                    Double.parseDouble(txtHourlyRate.getText())
            );

            if (EmployeeModel.addEmployee(employee)) {
                new Alert(Alert.AlertType.INFORMATION, "Employee Added Successfully!").show();
                loadAllEmployees();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Employee!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid number for hourly rate").show();
        }
    }

    @FXML
    void btnEmployeeDelete(ActionEvent event) {
        try {
            if (txtEmployeeId.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select an employee to delete").show();
                return;
            }

            if (EmployeeModel.deleteEmployee(txtEmployeeId.getText())) {
                new Alert(Alert.AlertType.INFORMATION, "Employee Deleted Successfully!").show();
                loadAllEmployees();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Delete Employee!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnEmployeeUpdate(ActionEvent event) {
        try {
            if (txtHourlyRate.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Hourly rate is required").show();
                return;
            }

            EmployeeDto employee = new EmployeeDto(
                    Integer.parseInt(txtEmployeeId.getText()),
                    txtEmployeeName.getText(),
                    txtEmployeeEmail.getText(),
                    txtEmployeePhone.getText(),
                    Double.parseDouble(txtHourlyRate.getText())
            );

            if (EmployeeModel.updateEmployee(employee)) {
                new Alert(Alert.AlertType.INFORMATION, "Employee Updated Successfully!").show();
                loadAllEmployees();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Update Employee!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid number for hourly rate").show();
        }
    }
    @FXML
    void btnEmployeeSearch(ActionEvent event) {
        try {
            if (txtEmployeeId.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please enter an employee ID to search").show();
                return;
            }

            EmployeeDto employee = EmployeeModel.searchEmployee(txtEmployeeId.getText());
            if (employee != null) {
                txtEmployeeName.setText(employee.getE_Name());
                txtEmployeeEmail.setText(employee.getE_Email());
                txtEmployeePhone.setText(employee.getE_Contact());
                txtHourlyRate.setText(String.valueOf(employee.getHourly_rate()));
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Employee Not Found!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnLoadAllEmployee(ActionEvent event) {
        loadAllEmployees();
    }

    @FXML
    void btnClearEmployeePage(ActionEvent event) {
        clearFields();
    }

    private void loadAllEmployees() {
        try {
            employeeList.clear();
            List<EmployeeDto> employees = EmployeeModel.getAllEmployees();
            employeeList.addAll(employees);
            tblEmployee.setItems(employeeList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load employees: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtEmployeeId.clear();
        txtEmployeeName.clear();
        txtEmployeeEmail.clear();
        txtEmployeePhone.clear();
        txtHourlyRate.clear();
        tblEmployee.getSelectionModel().clearSelection();
    }
}