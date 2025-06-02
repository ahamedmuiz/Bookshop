package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.dto.tm.InventoryTm;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class InventoryController {

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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private InventoryDto originalDto = null;

    public void initialize() {
        setCellValueFactory();
        loadSupplierIds();
        loadQuantityOperations();
        generateNextInventoryId();
        setListener();
        loadAllInventory();
        addTextValidations();
        dpLastUpdate.setValue(LocalDate.now());
        txtInventoryId.setEditable(false);
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
        cmbQuantityOperation.getItems().addAll("Add", "Subtract");
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
                try {
                    originalDto = new InventoryDto(
                            Integer.parseInt(newSelection.getInvId()),
                            Integer.parseInt(newSelection.getSupId()),
                            newSelection.getSupplierName(),
                            newSelection.getStockQty(),
                            newSelection.getLastUpdate(),
                            newSelection.getCategory(),
                            newSelection.getPrice()
                    );
                    setFields(originalDto);
                } catch (NumberFormatException e) {
                    showAlert("Error converting inventory data", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void calculateNewQuantity() {
        String operation = cmbQuantityOperation.getValue();
        String adjustmentText = txtQuantityAdjustment.getText();
        String currentQtyText = txtCurrentQuantity.getText();

        if (operation != null && !adjustmentText.isEmpty()) {
            try {
                int currentQty = currentQtyText.isEmpty() ? 0 : Integer.parseInt(currentQtyText);
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
                dpLastUpdate.setValue(LocalDate.now());
            }
        } catch (SQLException e) {
            showAlert("Failed to save inventory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btnUpdateInventory(ActionEvent event) {
        try {
            if (originalDto == null) {
                showAlert("Please select an item to update", Alert.AlertType.WARNING);
                return;
            }

            if (!validateForm()) return;

            InventoryDto updatedDto = getInventoryDto();

            // Check if any changes were made
            if (areDtosEqual(originalDto, updatedDto)) {
                showAlert("No changes detected. Nothing to update.", Alert.AlertType.INFORMATION);
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Update");
            confirmation.setHeaderText("Update Inventory Item");
            confirmation.setContentText("Are you sure you want to update this inventory item?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean isUpdated = InventoryModel.updateInventory(updatedDto);
                if (isUpdated) {
                    showAlert("Inventory updated successfully", Alert.AlertType.INFORMATION);
                    clearFields();
                    generateNextInventoryId();
                    loadAllInventory();
                    dpLastUpdate.setValue(LocalDate.now());
                    originalDto = null;
                }
            }
        } catch (SQLException e) {
            showAlert("Failed to update inventory: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean areDtosEqual(InventoryDto dto1, InventoryDto dto2) {
        return dto1.getInvId() == dto2.getInvId() &&
                dto1.getSupId() == dto2.getSupId() &&
                dto1.getSupplierName().equals(dto2.getSupplierName()) &&
                dto1.getStockQty() == dto2.getStockQty() &&
                dto1.getLastUpdate().equals(dto2.getLastUpdate()) &&
                dto1.getCategory().equals(dto2.getCategory()) &&
                Double.compare(dto1.getPrice(), dto2.getPrice()) == 0;
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
        // Create search dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Search Inventory");
        dialog.setHeaderText("Search by Inventory ID or Category");

        // Set the button types
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        // Create search fields
        TextField txtSearchId = new TextField();
        txtSearchId.setPromptText("Inventory ID");
        TextField txtSearchCategory = new TextField();
        txtSearchCategory.setPromptText("Category");

        VBox vbox = new VBox(10,
                new Label("Inventory ID:"), txtSearchId,
                new Label("Category:"), txtSearchCategory
        );
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        // Show dialog and wait for response
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == searchButtonType) {
            String searchId = txtSearchId.getText().trim();
            String searchCategory = txtSearchCategory.getText().trim();

            try {
                List<InventoryDto> inventoryList;
                if (!searchId.isEmpty()) {
                    inventoryList = InventoryModel.searchInventoryByIdOrName(searchId);
                } else if (!searchCategory.isEmpty()) {
                    inventoryList = InventoryModel.searchInventoryByAnyField(searchCategory);
                } else {
                    showAlert("Please enter either ID or Category to search", Alert.AlertType.WARNING);
                    return;
                }

                if (inventoryList.isEmpty()) {
                    showAlert("No matching records found", Alert.AlertType.INFORMATION);
                } else {
                    populateTable(inventoryList);
                    if (inventoryList.size() == 1) {
                        setFields(inventoryList.get(0));
                    }
                }
            } catch (SQLException e) {
                showAlert("Error searching inventory: " + e.getMessage(), Alert.AlertType.ERROR);
            }
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
        dpLastUpdate.setValue(LocalDate.now());
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
        txtCategory.clear();
        txtPrice.clear();
        tblInventory.getSelectionModel().clearSelection();
        originalDto = null;
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

    private boolean validateForm() {
        if (cmbSupplierId.getValue() == null || cmbSupplierId.getValue().isEmpty()) {
            showAlert("Please select a supplier", Alert.AlertType.WARNING);
            return false;
        }

        if (isEmpty(txtNewQuantity.getText())) {
            showAlert("Please enter a quantity", Alert.AlertType.WARNING);
            return false;
        }

        if (isEmpty(txtCategory.getText())) {
            showAlert("Please enter a category", Alert.AlertType.WARNING);
            return false;
        }

        if (isEmpty(txtPrice.getText())) {
            showAlert("Please enter a price", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private InventoryDto getInventoryDto() {
        LocalDate lastUpdate = dpLastUpdate.getValue() != null ? dpLastUpdate.getValue() : LocalDate.now();

        return new InventoryDto(
                Integer.parseInt(txtInventoryId.getText()),
                Integer.parseInt(cmbSupplierId.getValue()),
                txtSupplierName.getText(),
                Integer.parseInt(txtNewQuantity.getText()),
                lastUpdate,
                txtCategory.getText(),
                Double.parseDouble(txtPrice.getText())
        );
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
}