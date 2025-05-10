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
    private TextField txtEmployeeId;

    @FXML
    private TextField txtEmployeeName;

    @FXML
    private TextField txtEmployeeEmail;

    @FXML
    private TextField txtEmployeePhone;

    private final ObservableList<EmployeeDto> employeeList = FXCollections.observableArrayList();

    public void initialize() {
        // Mapped correctly to match EmployeeDto properties (CamelCase -> PascalCase)
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("E_ID"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("E_Name"));
        colEmployeeEmail.setCellValueFactory(new PropertyValueFactory<>("E_Email"));
        colEmployeePhone.setCellValueFactory(new PropertyValueFactory<>("E_Contact"));

    }

    @FXML
    void btnEmployeeAdd(ActionEvent event) {
        EmployeeDto employee = new EmployeeDto(
                Integer.parseInt(txtEmployeeId.getText()),
                txtEmployeeName.getText(),
                txtEmployeeEmail.getText(),
                txtEmployeePhone.getText()
        );

        try {
            if (EmployeeModel.addEmployee(employee)) {
                loadAllEmployees();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnEmployeeDelete(ActionEvent event) {
        try {
            if (EmployeeModel.deleteEmployee(txtEmployeeId.getText())) {
                loadAllEmployees();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnEmployeeUpdate(ActionEvent event) {
        EmployeeDto employee = new EmployeeDto(
                Integer.parseInt(txtEmployeeId.getText()),
                txtEmployeeName.getText(),
                txtEmployeeEmail.getText(),
                txtEmployeePhone.getText()
        );

        try {
            if (EmployeeModel.updateEmployee(employee)) {
                loadAllEmployees();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnEmployeeSearch(ActionEvent event) {
        try {
            EmployeeDto employee = EmployeeModel.searchEmployee(txtEmployeeId.getText());
            if (employee != null) {
                txtEmployeeName.setText(employee.getE_Name());
                txtEmployeeEmail.setText(employee.getE_Email());
                txtEmployeePhone.setText(employee.getE_Contact());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnLoadAllEmployee(ActionEvent event) {
        loadAllEmployees();
    }

    @FXML
    void btnClearEmployeePage(ActionEvent event) {
        txtEmployeeId.clear();
        txtEmployeeName.clear();
        txtEmployeeEmail.clear();
        txtEmployeePhone.clear();
        tblEmployee.getSelectionModel().clearSelection();
    }

    private void loadAllEmployees() {
        try {
            employeeList.clear();
            List<EmployeeDto> employees = EmployeeModel.getAllEmployees();
            employeeList.addAll(employees);
            tblEmployee.setItems(employeeList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
