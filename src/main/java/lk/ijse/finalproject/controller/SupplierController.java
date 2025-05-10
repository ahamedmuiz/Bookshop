package lk.ijse.finalproject.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.dto.SupplierDto;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.SupplierModel;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupplierController {

    @FXML private TextField txtSupplierId;
    @FXML private TextField txtName;
    @FXML private TextField txtContact;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<Integer> cmbSupplierId;
    @FXML private Label lblStatus;

    @FXML private TableView<SupplierDto> tblSupplier;
    @FXML private TableColumn<SupplierDto, String> colId;
    @FXML private TableColumn<SupplierDto, String> colName;
    @FXML private TableColumn<SupplierDto, String> colContact;
    @FXML private TableColumn<SupplierDto, String> colAddress;

    @FXML private TableView<InventoryDto> tblInventory;
    @FXML private TableColumn<InventoryDto, String> colInvId;
    @FXML private TableColumn<InventoryDto, String> colStockQty;
    @FXML private TableColumn<InventoryDto, String> colLastUpdate;
    @FXML private TableColumn<InventoryDto, String> colCategory;
    @FXML private TableColumn<InventoryDto, String> colPrice;

    private final SupplierModel supplierModel = new SupplierModel();
    private final InventoryModel inventoryModel = new InventoryModel();

    public void initialize() {
        setCellValueFactories();
        loadAllSuppliers();
        loadSupplierIds();

        cmbSupplierId.setOnAction(e -> {
            Integer selectedId = cmbSupplierId.getValue();
            if (selectedId != null) {
                try {
                    SupplierDto dto = supplierModel.searchSupplier(selectedId);
                    if (dto != null) {
                        txtSupplierId.setText(String.valueOf(dto.getSupId()));
                        txtName.setText(dto.getName());
                        txtContact.setText(dto.getContact());
                        txtAddress.setText(dto.getAddress());
                        loadInventoryForSupplier(selectedId);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void setCellValueFactories() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSupId())));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colContact.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContact()));
        colAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));

        colInvId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getInvId())));
        colStockQty.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStockQty())));
        colLastUpdate.setCellValueFactory(data -> {
            var date = data.getValue().getLastUpdate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        });
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice())));
    }

    private void loadAllSuppliers() {
        try {
            List<SupplierDto> list = supplierModel.getAllSuppliers();
            tblSupplier.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSupplierIds() {
        try {
            List<Integer> ids = supplierModel.getAllSupplierIds();
            cmbSupplierId.setItems(FXCollections.observableArrayList(ids));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventoryForSupplier(int supId) {
        try {
            List<InventoryDto> list = inventoryModel.getInventoryBySupplierId(supId);
            tblInventory.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSaveOnAction() {
        if (!validateInputs()) return;

        try {
            SupplierDto dto = new SupplierDto(
                    Integer.parseInt(txtSupplierId.getText()),
                    txtName.getText(),
                    txtContact.getText(),
                    txtAddress.getText()
            );

            boolean result = supplierModel.addSupplier(dto);
            lblStatus.setText(result ? "Supplier saved." : "Save failed.");
            if (result) {
                refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Database error.");
        }
    }

    @FXML
    void btnUpdateOnAction() {
        if (!validateInputs()) return;

        try {
            SupplierDto dto = new SupplierDto(
                    Integer.parseInt(txtSupplierId.getText()),
                    txtName.getText(),
                    txtContact.getText(),
                    txtAddress.getText()
            );

            boolean result = supplierModel.updateSupplier(dto);
            lblStatus.setText(result ? "Supplier updated." : "Update failed.");
            if (result) {
                refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Database error.");
        }
    }

    @FXML
    void btnDeleteOnAction() {
        String id = txtSupplierId.getText();
        if (id.isEmpty()) {
            lblStatus.setText("Enter ID to delete.");
            return;
        }

        try {
            boolean result = supplierModel.deleteSupplier(Integer.parseInt(id));
            lblStatus.setText(result ? "Supplier deleted." : "Delete failed.");
            if (result) {
                refresh();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Database error.");
        }
    }

    @FXML
    void btnSearchOnAction() {
        String id = txtSupplierId.getText();
        if (id.isEmpty()) {
            lblStatus.setText("Enter ID to search.");
            return;
        }

        try {
            SupplierDto dto = supplierModel.searchSupplier(Integer.parseInt(id));
            if (dto != null) {
                txtName.setText(dto.getName());
                txtContact.setText(dto.getContact());
                txtAddress.setText(dto.getAddress());
                cmbSupplierId.setValue(dto.getSupId());
                loadInventoryForSupplier(dto.getSupId());
                lblStatus.setText("Supplier found.");
            } else {
                lblStatus.setText("Supplier not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Search error.");
        }
    }

    @FXML
    void btnClearOnAction() {
        clearFields();
        lblStatus.setText("Fields cleared.");
    }

    private boolean validateInputs() {
        if (txtSupplierId.getText().isEmpty() || txtName.getText().isEmpty() ||
                txtContact.getText().isEmpty() || txtAddress.getText().isEmpty()) {
            lblStatus.setText("All fields are required.");
            return false;
        }

        try {
            Integer.parseInt(txtSupplierId.getText());
        } catch (NumberFormatException e) {
            lblStatus.setText("Supplier ID must be a number.");
            return false;
        }

        if (!txtContact.getText().matches("\\d{10}")) {
            lblStatus.setText("Contact must be a 10-digit number.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        txtSupplierId.clear();
        txtName.clear();
        txtContact.clear();
        txtAddress.clear();
        cmbSupplierId.setValue(null);
        tblInventory.getItems().clear();
    }

    private void refresh() {
        loadAllSuppliers();
        loadSupplierIds();
        clearFields();
    }
}



