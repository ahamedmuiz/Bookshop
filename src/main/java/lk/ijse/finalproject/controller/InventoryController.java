package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class InventoryController {

    @FXML private TextField txtInventoryId;
    @FXML private ComboBox<Integer> cmbSupplierId;
    @FXML private TextField txtQuantity;
    @FXML private DatePicker dpLastUpdate;
    @FXML private TextField txtCategory;
    @FXML private TextField txtPrice;

    @FXML private TableView<InventoryDto> tblInventory;
    @FXML private TableColumn<InventoryDto, Integer> colInventoryId;
    @FXML private TableColumn<InventoryDto, Integer> colSupplierId;
    @FXML private TableColumn<InventoryDto, Integer> colQuantity;
    @FXML private TableColumn<InventoryDto, LocalDate> colLastUpdate;
    @FXML private TableColumn<InventoryDto, String> colCategory;
    @FXML private TableColumn<InventoryDto, Double> colPrice;

    @FXML private Button btnAdd;

    private final InventoryModel inventoryModel = new InventoryModel();
    private final SupplierModel supplierModel = new SupplierModel();

    public void initialize() {
        colInventoryId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getInvId()).asObject());
        colSupplierId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getSupId()).asObject());
        colQuantity.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStockQty()).asObject());
        colLastUpdate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getLastUpdate()));
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        loadSupplierIds();

        cmbSupplierId.valueProperty().addListener((obs, oldVal, newVal) -> {
            btnAdd.setDisable(newVal == null);
        });

        btnAdd.setDisable(true);
        loadAllInventory();
    }

    private void loadSupplierIds() {
        try {
            List<Integer> ids = supplierModel.getAllSupplierIds();
            cmbSupplierId.setItems(FXCollections.observableArrayList(ids));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading supplier IDs: " + e.getMessage());
        }
    }

    @FXML
    void btnAddInventory(ActionEvent event) {
        try {
            InventoryDto dto = getInventoryDtoFromForm();
            if (inventoryModel.addInventory(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Inventory Added!");
                clearForm();
                loadAllInventory();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error adding inventory: " + e.getMessage());
        }
    }

    @FXML
    void btnUpdateInventory(ActionEvent event) {
        try {
            InventoryDto dto = getInventoryDtoFromForm();
            if (inventoryModel.updateInventory(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Inventory Updated!");
                clearForm();
                loadAllInventory();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error updating inventory: " + e.getMessage());
        }
    }

    @FXML
    void btnDeleteInventory(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtInventoryId.getText());
            if (inventoryModel.deleteInventory(id)) {
                showAlert(Alert.AlertType.INFORMATION, "Inventory Deleted!");
                clearForm();
                loadAllInventory();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error deleting inventory: " + e.getMessage());
        }
    }

    @FXML
    void btnSearchInventory(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtInventoryId.getText());
            InventoryDto dto = inventoryModel.searchInventory(id);
            if (dto != null) {
                cmbSupplierId.setValue(dto.getSupId());
                txtQuantity.setText(String.valueOf(dto.getStockQty()));
                dpLastUpdate.setValue(dto.getLastUpdate());
                txtCategory.setText(dto.getCategory());
                txtPrice.setText(String.valueOf(dto.getPrice()));
                tblInventory.setItems(FXCollections.observableArrayList(dto));
            } else {
                showAlert(Alert.AlertType.WARNING, "No inventory found!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error searching inventory: " + e.getMessage());
        }
    }

    @FXML
    public void btnClearForm(ActionEvent event) {
        clearForm();
        loadAllInventory();
    }

    @FXML
    public void btnLoadAllInventory(ActionEvent event) {
        loadAllInventory();
    }

    private InventoryDto getInventoryDtoFromForm() {
        return new InventoryDto(
                Integer.parseInt(txtInventoryId.getText()),
                cmbSupplierId.getValue(),
                Integer.parseInt(txtQuantity.getText()),
                dpLastUpdate.getValue(),
                txtCategory.getText(),
                Double.parseDouble(txtPrice.getText())
        );
    }

    private void clearForm() {
        txtInventoryId.clear();
        cmbSupplierId.getSelectionModel().clearSelection();
        txtQuantity.clear();
        dpLastUpdate.setValue(null);
        txtCategory.clear();
        txtPrice.clear();
    }

    private void loadAllInventory() {
        try {
            ObservableList<InventoryDto> list = FXCollections.observableArrayList(inventoryModel.getAllInventory());
            tblInventory.setItems(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading data: " + e.getMessage());
        }
    }

    public void refreshInventoryOnTabFocus() {
        loadAllInventory();
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }
}
