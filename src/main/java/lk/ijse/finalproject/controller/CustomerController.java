package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.CustomerDto;
import lk.ijse.finalproject.model.CustomerModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerController {

    @FXML private TextField txtCustomerId;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;

    @FXML private TableView<CustomerDto> tblCustomers;
    @FXML private TableColumn<CustomerDto, Integer> colId;
    @FXML private TableColumn<CustomerDto, String> colName;
    @FXML private TableColumn<CustomerDto, String> colEmail;
    @FXML private TableColumn<CustomerDto, String> colPhone;

    private CustomerDto currentCustomer;
    private final CustomerModel customerModel = new CustomerModel();

    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContact()));

        generateNextCustomerId();
        loadAllCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setCustomerData(newValue);
                        currentCustomer = newValue;
                    }
                });
    }

    private void setCustomerData(CustomerDto dto) {
        txtCustomerId.setText(String.valueOf(dto.getCId()));
        txtName.setText(dto.getName());
        txtEmail.setText(dto.getEmail());
        txtPhone.setText(dto.getContact());
    }

    private void generateNextCustomerId() {
        try {
            int nextId = customerModel.generateNextCustomerId();
            txtCustomerId.setText(String.valueOf(nextId));
            txtCustomerId.setEditable(false);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error generating customer ID: " + e.getMessage());
        }
    }

    private void loadAllCustomers() {
        try {
            ObservableList<CustomerDto> list = FXCollections.observableArrayList(customerModel.getAllCustomers());
            tblCustomers.setItems(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customers: " + e.getMessage());
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            CustomerDto dto = getCustomerDtoFromForm();
            if (customerModel.addCustomer(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Customer Added!");
                clearForm();
                loadAllCustomers();
                generateNextCustomerId();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            if (currentCustomer == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a customer to update!");
                return;
            }

            CustomerDto dto = getCustomerDtoFromForm();

            // Check if any fields have changed
            if (!isCustomerModified(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "No changes detected. Nothing was updated.");
                return;
            }

            if (customerModel.updateCustomer(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Customer Updated!");
                clearForm();
                loadAllCustomers();
                generateNextCustomerId();
                currentCustomer = null;
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    private boolean isCustomerModified(CustomerDto newDto) {
        return !(currentCustomer.getCId() == newDto.getCId() &&
                currentCustomer.getName().equals(newDto.getName()) &&
                currentCustomer.getEmail().equals(newDto.getEmail()) &&
                currentCustomer.getContact().equals(newDto.getContact()));
    }

    @FXML
    void handleDelete(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtCustomerId.getText());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Customer");
            alert.setContentText("Are you sure you want to delete this customer?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (customerModel.deleteCustomer(id)) {
                    showAlert(Alert.AlertType.INFORMATION, "Customer Deleted!");
                    clearForm();
                    loadAllCustomers();
                    generateNextCustomerId();
                    currentCustomer = null;
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Customer");
        dialog.setHeaderText("Search by ID or Name");
        dialog.setContentText("Enter ID or Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(searchTerm -> {
            try {
                List<CustomerDto> searchResults = customerModel.searchCustomersByIdOrName(searchTerm);
                if (searchResults.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "No customers found matching: " + searchTerm);
                } else {
                    setCustomerData(searchResults.get(0));
                    currentCustomer = searchResults.get(0);
                    tblCustomers.setItems(FXCollections.observableArrayList(searchResults));
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error searching customers: " + e.getMessage());
            }
        });
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        clearForm();
        tblCustomers.getSelectionModel().clearSelection();
        generateNextCustomerId();
        currentCustomer = null;
    }

    @FXML
    void handleLoadAll(ActionEvent event) {
        loadAllCustomers();
    }

    private CustomerDto getCustomerDtoFromForm() {
        return new CustomerDto(
                Integer.parseInt(txtCustomerId.getText()),
                txtName.getText(),
                txtEmail.getText(),
                txtPhone.getText()
        );
    }

    private void clearForm() {
        txtCustomerId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void refreshCustomerTableOnTabFocus() {
        loadAllCustomers();
    }
}