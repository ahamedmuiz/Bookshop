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

    private final CustomerModel customerModel = new CustomerModel();

    public void initialize() {

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContact()));
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            CustomerDto dto = getCustomerDtoFromForm();
            if (customerModel.addCustomer(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Customer Added!");
                clearForm();
                handleLoadAll(null);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            CustomerDto dto = getCustomerDtoFromForm();
            if (customerModel.updateCustomer(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Customer Updated!");
                clearForm();
                handleLoadAll(null);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtCustomerId.getText());
            if (customerModel.deleteCustomer(id)) {
                showAlert(Alert.AlertType.INFORMATION, "Customer Deleted!");
                clearForm();
                handleLoadAll(null);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtCustomerId.getText());
            CustomerDto dto = customerModel.searchCustomer(id);
            if (dto != null) {
                txtName.setText(dto.getName());
                txtEmail.setText(dto.getEmail());
                txtPhone.setText(dto.getContact());
                tblCustomers.setItems(FXCollections.observableArrayList(dto));
            } else {
                showAlert(Alert.AlertType.WARNING, "Customer Not Found!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        clearForm();
        tblCustomers.setItems(FXCollections.observableArrayList());
    }

    @FXML
    void handleLoadAll(ActionEvent event) {
        try {
            ObservableList<CustomerDto> list = FXCollections.observableArrayList(customerModel.getAllCustomers());
            tblCustomers.setItems(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading customers: " + e.getMessage());
        }
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
        new Alert(type, message).show();
    }

    public void refreshCustomerTableOnTabFocus() {
        handleLoadAll(null);
    }
}
