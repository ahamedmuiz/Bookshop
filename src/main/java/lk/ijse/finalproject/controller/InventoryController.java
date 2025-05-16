package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.dto.tm.InventoryTm;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InventoryController {

    @FXML private Button btnAdd;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;
    @FXML private Button btnLoadAll;
    @FXML private Button btnSearch;
    @FXML private Button btnSearchAnyField;
    @FXML private Button btnUpdate;
    @FXML private ComboBox<String> cmbQuantityOperation;
    @FXML private ComboBox<String> cmbSupplierId;
    @FXML private TableColumn<?, ?> colCategory;
    @FXML private TableColumn<?, ?> colInventoryId;
    @FXML private TableColumn<?, ?> colLastUpdate;
    @FXML private TableColumn<?, ?> colPrice;
    @FXML private TableColumn<?, ?> colQuantity;
    @FXML private TableColumn<?, ?> colSupplierId;
    @FXML private TableColumn<?, ?> colSupplierName;
    @FXML private DatePicker dpLastUpdate;
    @FXML private TableView<InventoryTm> tblInventory;
    @FXML private TextField txtCategory;
    @FXML private TextField txtCurrentQuantity;
    @FXML private TextField txtInventoryId;
    @FXML private TextField txtNewQuantity;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantityAdjustment;
    @FXML private TextField txtSearch;
    @FXML private TextField txtSupplierName;

    public void initialize() {
        setCellValueFactory();
        loadSupplierIds();
        loadQuantityOperations();
        generateNextInventoryId();
        setListener();
        loadAllInventory();

        // Add table selection listener
        tblInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setFields(newSelection);
            }
        });
    }


    private void setCellValueFactory() {
        colInventoryId.setCellValueFactory(new PropertyValueFactory<>("invId"));
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supId"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        colLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadSupplierIds() {
        try {
            List<Integer> supplierIds = SupplierModel.getAllSupplierIds();
            cmbSupplierId.getItems().clear();
            supplierIds.forEach(id -> cmbSupplierId.getItems().add(String.valueOf(id)));
        } catch (SQLException e) {
            showAlert("Failed to load supplier IDs", Alert.AlertType.ERROR);
        }
    }

    private void loadQuantityOperations() {
        cmbQuantityOperation.getItems().addAll("Add", "Subtract", "Set");
        cmbQuantityOperation.getSelectionModel().selectFirst();
    }

    private void generateNextInventoryId() {
        try {
            String nextId = InventoryModel.generateNextInventoryId();
            txtInventoryId.setText(nextId);
        } catch (SQLException e) {
            showAlert("Failed to generate ID", Alert.AlertType.ERROR);
        }
    }

    private void setListener() {
        cmbSupplierId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String supplierName = SupplierModel.getSupplierName(newValue);
                    txtSupplierName.setText(supplierName);
                } catch (SQLException e) {
                    showAlert("Failed to get supplier name", Alert.AlertType.ERROR);
                }
            }
            updateButtonStates();
        });

        cmbQuantityOperation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            calculateNewQuantity();
        });

        txtQuantityAdjustment.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateNewQuantity();
        });

        txtCurrentQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateNewQuantity();
        });

        tblInventory.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setFields(newSelection);
            }
            updateButtonStates();
        });
    }


    private void calculateNewQuantity() {
        String operation = cmbQuantityOperation.getValue();
        String adjustmentText = txtQuantityAdjustment.getText();
        String currentQtyText = txtCurrentQuantity.getText();

        if (operation != null && !adjustmentText.isEmpty() && !currentQtyText.isEmpty()) {
            try {
                int currentQty = Integer.parseInt(currentQtyText);
                int adjustment = Integer.parseInt(adjustmentText);
                int newQty = currentQty;

                switch (operation) {
                    case "Add":
                        newQty = currentQty + adjustment;
                        break;
                    case "Subtract":
                        newQty = currentQty - adjustment;
                        if (newQty < 0) {
                            showAlert("Cannot have negative quantity!", Alert.AlertType.WARNING);
                            return;
                        }
                        break;
                    case "Set":
                        newQty = adjustment;
                        break;
                }

                txtNewQuantity.setText(String.valueOf(newQty));
            } catch (NumberFormatException e) {
                txtNewQuantity.clear();
            }
        }
    }

    private void addTextValidations() {
        txtQuantityAdjustment.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txtQuantityAdjustment.setText(oldValue);
            }
        });

        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrice.setText(oldValue);
            }
        });
    }

    private void setupButtonStates() {
        boolean formValid = cmbSupplierId.getValue() != null &&
                !isEmpty(txtNewQuantity.getText()) &&
                !isEmpty(txtCategory.getText()) &&
                !isEmpty(txtPrice.getText());

        boolean hasSelection = tblInventory.getSelectionModel().getSelectedItem() != null;

        btnAdd.setDisable(!formValid || hasSelection);
        btnUpdate.setDisable(!formValid || !hasSelection);
        btnDelete.setDisable(!hasSelection);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    @FXML
    void btnAddInventory(ActionEvent event) {
        try {
            if (!validateForm()) return;

            InventoryDto dto = getInventoryDto();
            boolean isSaved = InventoryModel.saveInventory(dto);
            if (isSaved) {
                showAlert("Inventory added successfully", Alert.AlertType.INFORMATION);
                clearFields();
                generateNextInventoryId();
                loadAllInventory();
            }
        } catch (SQLException e) {
            showAlert("Failed to save inventory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }




    @FXML
    void btnUpdateInventory(ActionEvent event) {
        try {
            InventoryTm selectedItem = tblInventory.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                showAlert("Please select an item to update", Alert.AlertType.WARNING);
                return;
            }

            if (!validateForm()) return;

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Update");
            confirmation.setHeaderText("Update Inventory Item");
            confirmation.setContentText("Are you sure you want to update this inventory item?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                InventoryDto dto = getInventoryDto();
                boolean isUpdated = InventoryModel.updateInventory(dto);
                if (isUpdated) {
                    showAlert("Inventory updated successfully", Alert.AlertType.INFORMATION);
                    clearFields();
                    generateNextInventoryId();
                    loadAllInventory();
                }
            }
        } catch (SQLException e) {
            showAlert("Failed to update inventory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btnDeleteInventory(ActionEvent event) {
        InventoryTm selectedItem = tblInventory.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("Please select an item to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Inventory Item");
        confirmation.setContentText("Are you sure you want to delete this inventory item?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = InventoryModel.deleteInventory(selectedItem.getInvId());
                if (isDeleted) {
                    showAlert("Inventory deleted successfully", Alert.AlertType.INFORMATION);
                    clearFields();
                    generateNextInventoryId();
                    loadAllInventory();
                }
            } catch (SQLException e) {
                showAlert("Failed to delete inventory: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void btnSearchInventory(ActionEvent event) {
        String invId = txtInventoryId.getText();
        if (isEmpty(invId)) {
            showAlert("Please enter an inventory ID", Alert.AlertType.ERROR);
            return;
        }

        try {
            InventoryDto dto = InventoryModel.searchInventory(invId);
            if (dto != null) {
                setFields(dto);
                selectTableRow(dto);
            } else {
                showAlert("Inventory not found", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    void btnSearchAnyField(ActionEvent event) {
        String searchText = txtSearch.getText();
        if (isEmpty(searchText)) {
            showAlert("Please enter a search term", Alert.AlertType.ERROR);
            return;
        }

        try {
            List<InventoryDto> inventoryList = InventoryModel.searchInventoryByAnyField(searchText);
            if (inventoryList.isEmpty()) {
                showAlert("No matching records found", Alert.AlertType.INFORMATION);
            } else {
                populateTable(inventoryList);
            }
        } catch (SQLException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btnClearForm(ActionEvent event) {
        clearFields();
        generateNextInventoryId();
    }

    @FXML
    void btnLoadAllInventory(ActionEvent event) {
        loadAllInventory();
    }

    @FXML
    void btnclearField(ActionEvent event) {
        txtSearch.clear();
        loadAllInventory();
    }

    private void setFields(InventoryTm tm) {
        txtInventoryId.setText(tm.getInvId());
        cmbSupplierId.setValue(tm.getSupId());
        txtSupplierName.setText(tm.getSupplierName());
        txtCurrentQuantity.setText(String.valueOf(tm.getStockQty()));
        txtNewQuantity.setText(String.valueOf(tm.getStockQty()));
        dpLastUpdate.setValue(tm.getLastUpdate());
        txtCategory.setText(tm.getCategory());
        txtPrice.setText(String.valueOf(tm.getPrice()));
    }

    private void setFields(InventoryDto dto) {
        txtInventoryId.setText(String.valueOf(dto.getInvId()));
        cmbSupplierId.setValue(String.valueOf(dto.getSupId()));
        txtSupplierName.setText(dto.getSupplierName());
        txtCurrentQuantity.setText(String.valueOf(dto.getStockQty()));
        txtNewQuantity.setText(String.valueOf(dto.getStockQty()));
        dpLastUpdate.setValue(dto.getLastUpdate());
        txtCategory.setText(dto.getCategory());
        txtPrice.setText(String.valueOf(dto.getPrice()));
    }

    private void clearFields() {
        txtInventoryId.clear();
        cmbSupplierId.getSelectionModel().clearSelection();
        txtSupplierName.clear();
        txtCurrentQuantity.clear();
        txtNewQuantity.clear();
        txtQuantityAdjustment.clear();
        cmbQuantityOperation.getSelectionModel().selectFirst();
        dpLastUpdate.setValue(null);
        txtCategory.clear();
        txtPrice.clear();
        tblInventory.getSelectionModel().clearSelection();
    }

    private void loadAllInventory() {
        try {
            List<InventoryDto> inventoryList = InventoryModel.getAllInventory();
            populateTable(inventoryList);
        } catch (SQLException e) {
            showAlert(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateTable(List<InventoryDto> inventoryList) {
        ObservableList<InventoryTm> obList = FXCollections.observableArrayList();

        for (InventoryDto dto : inventoryList) {
            obList.add(new InventoryTm(
                    String.valueOf(dto.getInvId()),
                    String.valueOf(dto.getSupId()),
                    dto.getSupplierName(),
                    dto.getStockQty(),
                    dto.getLastUpdate(),
                    dto.getCategory(),
                    dto.getPrice(),
                    new Button("Delete")
            ));
        }

        tblInventory.setItems(obList);
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        new Alert(alertType, message).show();
    }

    private void updateButtonStates() {
        boolean isFormValid = isFormValid();
        boolean hasTableSelection = tblInventory.getSelectionModel().getSelectedItem() != null;

        // Enable/disable buttons based on form state and selection
        btnAdd.setDisable(!isFormValid || hasTableSelection);
        btnUpdate.setDisable(!isFormValid || !hasTableSelection);
        btnDelete.setDisable(!hasTableSelection);
        btnClear.setDisable(!isFormValid && !hasTableSelection);

        // Search buttons are always enabled
        btnSearch.setDisable(false);
        btnSearchAnyField.setDisable(false);
        btnLoadAll.setDisable(false);
    }

    private boolean isFormValid() {
        // Check all required fields are filled and valid
        return cmbSupplierId.getValue() != null &&
                !txtNewQuantity.getText().isEmpty() &&
                !txtCategory.getText().isEmpty() &&
                !txtPrice.getText().isEmpty() &&
                isValidNumber(txtNewQuantity.getText()) &&
                isValidDecimal(txtPrice.getText());
    }

    private boolean isValidNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDecimal(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateForm() {
        // Validate supplier selection
        if (cmbSupplierId.getValue() == null || cmbSupplierId.getValue().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a supplier").show();
            return false;
        }

        // Validate quantity
        if (txtNewQuantity.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a quantity").show();
            return false;
        }
        try {
            Integer.parseInt(txtNewQuantity.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Quantity must be a valid number").show();
            return false;
        }

        // Validate category
        if (txtCategory.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a category").show();
            return false;
        }

        // Validate price
        if (txtPrice.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a price").show();
            return false;
        }
        try {
            Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Price must be a valid number").show();
            return false;
        }

        return true;
    }

    private InventoryDto getInventoryDto() {
        return new InventoryDto(
                Integer.parseInt(txtInventoryId.getText()),
                Integer.parseInt(cmbSupplierId.getValue()),
                txtSupplierName.getText(),
                Integer.parseInt(txtNewQuantity.getText()),
                dpLastUpdate.getValue() != null ? dpLastUpdate.getValue() : LocalDate.now(),
                txtCategory.getText(),
                Double.parseDouble(txtPrice.getText())
        );
    }

    private void selectTableRow(InventoryDto dto) {
        tblInventory.getItems().stream()
                .filter(item -> item.getInvId().equals(String.valueOf(dto.getInvId())))
                .findFirst()
                .ifPresent(item -> {
                    tblInventory.getSelectionModel().select(item);
                    tblInventory.scrollTo(item);
                });
    }
}