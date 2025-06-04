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
import java.util.Optional;
import java.util.regex.Pattern;

public class EmployeeController {

    @FXML private TableView<EmployeeDto> tblEmployee;
    @FXML private TableColumn<EmployeeDto, Integer> colEmployeeId;
    @FXML private TableColumn<EmployeeDto, String> colEmployeeName;
    @FXML private TableColumn<EmployeeDto, String> colEmployeeEmail;
    @FXML private TableColumn<EmployeeDto, String> colEmployeePhone;
    @FXML private TableColumn<EmployeeDto, Double> colHourlyRate;

    @FXML private TextField txtEmployeeId;
    @FXML private TextField txtEmployeeName;
    @FXML private TextField txtEmployeeEmail;
    @FXML private TextField txtEmployeePhone;
    @FXML private TextField txtHourlyRate;

    private final ObservableList<EmployeeDto> employeeList = FXCollections.observableArrayList();
    private EmployeeDto currentEmployee;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{3,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern HOURLY_RATE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");

    public void initialize() {
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("E_ID"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("E_Name"));
        colEmployeeEmail.setCellValueFactory(new PropertyValueFactory<>("E_Email"));
        colEmployeePhone.setCellValueFactory(new PropertyValueFactory<>("E_Contact"));
        colHourlyRate.setCellValueFactory(new PropertyValueFactory<>("hourly_rate"));

        generateNextEmployeeId();
        loadAllEmployees();

        tblEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setEmployeeData(newSelection);
                currentEmployee = newSelection;
            }
        });
    }

    private void generateNextEmployeeId() {
        try {
            int nextId = EmployeeModel.generateNextEmployeeId();
            txtEmployeeId.setText(String.valueOf(nextId));
            txtEmployeeId.setEditable(false);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error generating employee ID: " + e.getMessage());
        }
    }

    private void setEmployeeData(EmployeeDto dto) {
        txtEmployeeId.setText(String.valueOf(dto.getE_ID()));
        txtEmployeeName.setText(dto.getE_Name());
        txtEmployeeEmail.setText(dto.getE_Email());
        txtEmployeePhone.setText(dto.getE_Contact());
        txtHourlyRate.setText(String.valueOf(dto.getHourly_rate()));
    }

    @FXML
    void btnEmployeeAdd(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            EmployeeDto employee = getEmployeeDtoFromForm();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Add");
            confirmAlert.setHeaderText("Add New Employee");
            confirmAlert.setContentText("Are you sure you want to add this employee?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (EmployeeModel.addEmployee(employee)) {
                    showAlert(Alert.AlertType.INFORMATION, "Employee Added Successfully!");
                    loadAllEmployees();
                    clearFields();
                    generateNextEmployeeId();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to Add Employee!");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnEmployeeDelete(ActionEvent event) {
        try {
            if (txtEmployeeId.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select an employee to delete");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Employee");
            confirmAlert.setContentText("Are you sure you want to delete this employee?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (EmployeeModel.deleteEmployee(txtEmployeeId.getText())) {
                    showAlert(Alert.AlertType.INFORMATION, "Employee Deleted Successfully!");
                    loadAllEmployees();
                    clearFields();
                    generateNextEmployeeId();
                    currentEmployee = null;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to Delete Employee!");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnEmployeeUpdate(ActionEvent event) {
        try {
            if (currentEmployee == null) {
                showAlert(Alert.AlertType.WARNING, "Please select an employee to update");
                return;
            }

            if (!validateFields()) {
                return;
            }

            EmployeeDto employee = getEmployeeDtoFromForm();

            if (!isEmployeeModified(employee)) {
                showAlert(Alert.AlertType.INFORMATION, "No changes detected");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Update");
            confirmAlert.setHeaderText("Update Employee");
            confirmAlert.setContentText("Are you sure you want to update this employee?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (EmployeeModel.updateEmployee(employee)) {
                    showAlert(Alert.AlertType.INFORMATION, "Employee Updated Successfully!");
                    loadAllEmployees();
                    clearFields();
                    generateNextEmployeeId();
                    currentEmployee = null;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to Update Employee!");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isEmployeeModified(EmployeeDto newDto) {
        return !(currentEmployee.getE_ID() == newDto.getE_ID() &&
                currentEmployee.getE_Name().equals(newDto.getE_Name()) &&
                currentEmployee.getE_Email().equals(newDto.getE_Email()) &&
                currentEmployee.getE_Contact().equals(newDto.getE_Contact()) &&
                currentEmployee.getHourly_rate() == newDto.getHourly_rate());
    }

    @FXML
    void btnEmployeeSearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Employee");
        dialog.setHeaderText("Search by ID or Name");
        dialog.setContentText("Enter ID or Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(searchTerm -> {
            try {
                List<EmployeeDto> searchResults = EmployeeModel.searchEmployeesByIdOrName(searchTerm);
                if (searchResults.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "No employees found matching: " + searchTerm);
                } else {
                    setEmployeeData(searchResults.get(0));
                    currentEmployee = searchResults.get(0);
                    tblEmployee.setItems(FXCollections.observableArrayList(searchResults));
                }
            } catch (SQLException | ClassNotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Error searching employees: " + e.getMessage());
            }
        });
    }

    @FXML
    void btnLoadAllEmployee(ActionEvent event) {
        loadAllEmployees();
    }

    @FXML
    void btnClearEmployeePage(ActionEvent event) {
        clearFields();
        generateNextEmployeeId();
    }

    private void loadAllEmployees() {
        try {
            employeeList.clear();
            List<EmployeeDto> employees = EmployeeModel.getAllEmployees();
            employeeList.addAll(employees);
            tblEmployee.setItems(employeeList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load employees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtEmployeeName.clear();
        txtEmployeeEmail.clear();
        txtEmployeePhone.clear();
        txtHourlyRate.clear();
        tblEmployee.getSelectionModel().clearSelection();
    }

    private EmployeeDto getEmployeeDtoFromForm() {
        return new EmployeeDto(
                Integer.parseInt(txtEmployeeId.getText()),
                txtEmployeeName.getText(),
                txtEmployeeEmail.getText(),
                txtEmployeePhone.getText(),
                Double.parseDouble(txtHourlyRate.getText())
        );
    }

    private boolean validateFields() {
        // Validate Name
        if (txtEmployeeName.getText().isEmpty() || !NAME_PATTERN.matcher(txtEmployeeName.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name!\n" +
                    "- Must be 3-50 characters long\n" +
                    "- Can only contain letters and spaces");
            txtEmployeeName.requestFocus();
            return false;
        }

        // Validate Email
        if (txtEmployeeEmail.getText().isEmpty() || !EMAIL_PATTERN.matcher(txtEmployeeEmail.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email Address!\n" +
                    "Please enter a valid email (e.g., example@domain.com)");
            txtEmployeeEmail.requestFocus();
            return false;
        }

        // Validate Phone
        if (txtEmployeePhone.getText().isEmpty() || !PHONE_PATTERN.matcher(txtEmployeePhone.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number!\n" +
                    "- Must be 10 digits\n" +
                    "- Must start with 0 (e.g., 0771234567)");
            txtEmployeePhone.requestFocus();
            return false;
        }

        // Validate Hourly Rate
        if (txtHourlyRate.getText().isEmpty() || !HOURLY_RATE_PATTERN.matcher(txtHourlyRate.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Hourly Rate!\n" +
                    "- Must be a positive number\n" +
                    "- Can have up to 2 decimal places\n" +
                    "Examples: 15, 12.50, 10.75");
            txtHourlyRate.requestFocus();
            return false;
        }

        double rate = Double.parseDouble(txtHourlyRate.getText());
        if (rate <= 0) {
            showAlert(Alert.AlertType.ERROR, "Hourly Rate must be greater than 0");
            txtHourlyRate.requestFocus();
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}