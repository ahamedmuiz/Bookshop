package lk.ijse.finalproject.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.dto.SupplierDto;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InventoryController {
    @FXML private TextField txtInventoryId;
    @FXML private ComboBox<Integer> cmbSupplierId;
    @FXML private TextField txtSupplierName;
    @FXML private TextField txtCurrentQuantity;
    @FXML private TextField txtQuantityAdjustment;
    @FXML private ComboBox<String> cmbQuantityOperation;
    @FXML private TextField txtNewQuantity;
    @FXML private DatePicker dpLastUpdate;
    @FXML private TextField txtCategory;
    @FXML private TextField txtPrice;
    @FXML private TableView<InventoryDto> tblInventory;
    @FXML private TableColumn<InventoryDto, Integer> colInventoryId;
    @FXML private TableColumn<InventoryDto, Integer> colSupplierId;
    @FXML private TableColumn<InventoryDto, String> colSupplierName;
    @FXML private TableColumn<InventoryDto, Integer> colQuantity;
    @FXML private TableColumn<InventoryDto, LocalDate> colLastUpdate;
    @FXML private TableColumn<InventoryDto, String> colCategory;
    @FXML private TableColumn<InventoryDto, Double> colPrice;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnSearch;
    @FXML private Button btnClear;
    @FXML private Button btnLoadAll;
    @FXML private TextField txtSearch;

    private final InventoryModel inventoryModel = new InventoryModel();
    private final SupplierModel supplierModel = new SupplierModel();

    public void initialize() {
        initializeTableColumns();
        setupForm();
        loadAllInventory();
        setupTableSelectionListener();
        setupQuantityUpdateListener();
        addTextValidations();
    }

    private void initializeTableColumns() {
        colInventoryId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getInvId()).asObject());
        colSupplierId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getSupId()).asObject());
        colSupplierName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSupplierName()));
        colQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getStockQty()).asObject());
        colLastUpdate.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getLastUpdate()));
        colCategory.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory()));
        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
    }

    private void setupForm() {
        generateNewInventoryId();
        txtInventoryId.setEditable(false);
        dpLastUpdate.setValue(LocalDate.now());
        loadSupplierIds();
        setupSupplierIdListener();

        cmbQuantityOperation.setItems(FXCollections.observableArrayList("Add", "Subtract", "Set"));
        cmbQuantityOperation.getSelectionModel().selectFirst();

        clearFormFields();
        setupButtonStates();
    }

    private void clearFormFields() {
        txtSupplierName.clear();
        txtCurrentQuantity.clear();
        txtQuantityAdjustment.clear();
        txtNewQuantity.clear();
        txtCategory.clear();
        txtPrice.clear();
    }

    private void setupQuantityUpdateListener() {
        cmbQuantityOperation.valueProperty().addListener((obs, oldVal, newVal) -> calculateNewQuantity());
        txtQuantityAdjustment.textProperty().addListener((obs, oldVal, newVal) -> calculateNewQuantity());
        txtCurrentQuantity.textProperty().addListener((obs, oldVal, newVal) -> calculateNewQuantity());
    }

    private void calculateNewQuantity() {
        try {
            String currentQtyText = txtCurrentQuantity.getText();
            String adjustmentText = txtQuantityAdjustment.getText();

            if (isEmptyOrNull(currentQtyText) || isEmptyOrNull(adjustmentText)) {
                txtNewQuantity.clear();
                return;
            }

            int currentQty = Integer.parseInt(currentQtyText);
            int adjustment = Integer.parseInt(adjustmentText);
            String operation = cmbQuantityOperation.getValue();

            int newQty;
            switch (operation) {
                case "Add":
                    newQty = currentQty + adjustment;
                    break;
                case "Subtract":
                    newQty = currentQty - adjustment;
                    if (newQty < 0) {
                        showAlert(Alert.AlertType.WARNING, "Cannot have negative quantity!");
                        return;
                    }
                    break;
                case "Set":
                    newQty = adjustment;
                    break;
                default:
                    newQty = currentQty;
            }

            txtNewQuantity.setText(String.valueOf(newQty));
        } catch (NumberFormatException e) {
            txtNewQuantity.clear();
        }
    }

    private void generateNewInventoryId() {
        try {
            int nextId = inventoryModel.generateNextInventoryId();
            txtInventoryId.setText(String.valueOf(nextId));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error generating inventory ID: " + e.getMessage());
            txtInventoryId.setText("");
        }
    }

    private void loadSupplierIds() {
        try {
            List<Integer> ids = supplierModel.getAllSupplierIds();
            cmbSupplierId.setItems(FXCollections.observableArrayList(ids));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading supplier IDs: " + e.getMessage());
        }
    }

    private void setupSupplierIdListener() {
        cmbSupplierId.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    SupplierDto supplier = supplierModel.searchSupplier(newVal);
                    txtSupplierName.setText(supplier != null ? supplier.getName() : "");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error loading supplier details: " + e.getMessage());
                    txtSupplierName.setText("");
                }
            } else {
                txtSupplierName.setText("");
            }
            updateButtonStates();
        });
    }

    private void setupButtonStates() {
        btnAdd.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        cmbSupplierId.valueProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtCategory.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtPrice.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        txtNewQuantity.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());

        tblInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
        });
    }

    private void updateButtonStates() {
        boolean formValid = cmbSupplierId.getValue() != null &&
                !isEmptyOrNull(txtNewQuantity.getText()) &&
                !isEmptyOrNull(txtCategory.getText()) &&
                !isEmptyOrNull(txtPrice.getText());

        boolean hasSelection = !tblInventory.getSelectionModel().isEmpty();

        btnAdd.setDisable(!formValid || hasSelection);
        btnUpdate.setDisable(!formValid || !hasSelection);
        btnDelete.setDisable(!hasSelection);
    }

    private boolean isEmptyOrNull(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void addTextValidations() {
        txtQuantityAdjustment.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*")) {
                txtQuantityAdjustment.setText(oldVal != null ? oldVal : "");
            }
        });

        txtPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*(\\.\\d*)?")) {
                txtPrice.setText(oldVal != null ? oldVal : "");
            }
        });
    }

    @FXML
    void btnAddInventory(ActionEvent event) {
        try {
            InventoryDto dto = getInventoryDtoFromForm();
            boolean isSaved = inventoryModel.addInventory(dto);

            if (isSaved) {
                showDetailedAlert(Alert.AlertType.INFORMATION, "Success",
                        "Inventory Added", "Inventory item added successfully!");
                clearForm();
                loadAllInventory();
            }
        } catch (SQLException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error adding inventory", e.getMessage());
        } catch (IllegalArgumentException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Input Error",
                    "Invalid data", e.getMessage());
        }
    }

    @FXML
    void btnUpdateInventory(ActionEvent event) {
        try {
            if (tblInventory.getSelectionModel().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select an item from the table to update");
                return;
            }

            if (isEmptyOrNull(txtQuantityAdjustment.getText())) {
                showAlert(Alert.AlertType.WARNING, "Please enter a quantity adjustment value");
                return;
            }

            InventoryDto selectedItem = tblInventory.getSelectionModel().getSelectedItem();
            String operation = cmbQuantityOperation.getValue();
            int adjustment = Integer.parseInt(txtQuantityAdjustment.getText());
            int currentQty = selectedItem.getStockQty();

            // Create updated DTO with all fields
            InventoryDto updatedDto = new InventoryDto(
                    selectedItem.getInvId(),
                    cmbSupplierId.getValue(),
                    txtSupplierName.getText(),
                    adjustment,
                    LocalDate.now(),
                    txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText())
            );

            // Calculate new quantity for confirmation message
            int newQty = calculateNewQuantity(operation, currentQty, adjustment);
            if (newQty < 0) {
                showAlert(Alert.AlertType.WARNING, "Cannot have negative quantity!");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Update");
            confirmation.setHeaderText("Update Inventory Item");
            confirmation.setContentText(String.format(
                    "Operation: %s %d\nCurrent Qty: %d → New Qty: %d\n\nConfirm update?",
                    operation, adjustment, currentQty, newQty
            ));

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                boolean isUpdated;

                if (operation.equals("Set")) {
                    updatedDto.setStockQty(adjustment);
                    isUpdated = inventoryModel.updateInventory(updatedDto);
                } else {
                    isUpdated = inventoryModel.updateInventoryWithStockChange(
                            updatedDto,
                            operation.equals("Add")
                    );
                }

                if (isUpdated) {
                    showDetailedAlert(Alert.AlertType.INFORMATION, "Success",
                            "Inventory Updated", "Inventory item updated successfully!");
                    clearForm();
                    loadAllInventory();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update failed!");
                }
            }
        } catch (SQLException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error updating inventory", e.getMessage());
        } catch (NumberFormatException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Input Error",
                    "Invalid numeric values", "Please check your input values");
        }
    }

    private int calculateNewQuantity(String operation, int currentQty, int adjustment) {
        switch (operation) {
            case "Add":
                return currentQty + adjustment;
            case "Subtract":
                return currentQty - adjustment;
            case "Set":
                return adjustment;
            default:
                return currentQty;
        }
    }

    @FXML
    void btnDeleteInventory(ActionEvent event) {
        try {
            if (tblInventory.getSelectionModel().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please select an item from the table to delete");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this inventory item?\nThis action cannot be undone.",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                int id = Integer.parseInt(txtInventoryId.getText());
                boolean isDeleted = inventoryModel.deleteInventory(id);

                if (isDeleted) {
                    showDetailedAlert(Alert.AlertType.INFORMATION, "Success",
                            "Inventory Deleted", "Inventory item deleted successfully!");
                    clearForm();
                    loadAllInventory();
                }
            }
        } catch (SQLException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error deleting inventory", e.getMessage());
        } catch (NumberFormatException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Input Error",
                    "Invalid inventory ID", "Please select a valid inventory item");
        }
    }

    @FXML
    void btnSearchInventory(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Inventory");
        dialog.setHeaderText("Enter Inventory ID");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(idText -> {
            try {
                int id = Integer.parseInt(idText);
                InventoryDto dto = inventoryModel.searchInventory(id);

                if (dto != null) {
                    fillFormWithInventory(dto);
                    selectMatchingTableRow(dto);
                } else {
                    showAlert(Alert.AlertType.WARNING, "No inventory found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                showDetailedAlert(Alert.AlertType.ERROR, "Input Error",
                        "Invalid ID", "Please enter a valid numeric ID");
            } catch (SQLException e) {
                showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                        "Search error", e.getMessage());
            }
        });
    }

    @FXML
    void btnClearForm(ActionEvent event) {
        clearForm();
    }

    @FXML
    void btnLoadAllInventory(ActionEvent event) {
        loadAllInventory();
    }

    @FXML
    void btnSearchAnyField(ActionEvent event) {
        String searchText = txtSearch.getText();
        if (isEmptyOrNull(searchText)) {
            showAlert(Alert.AlertType.WARNING, "Please enter search criteria");
            return;
        }

        try {
            List<InventoryDto> results = inventoryModel.searchInventoryByAnyField(searchText.trim());
            if (results.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No matching inventory found");
            } else {
                tblInventory.setItems(FXCollections.observableArrayList(results));
            }
        } catch (SQLException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                    "Search error", e.getMessage());
        }
    }

    @FXML
    void btnClearSearch(ActionEvent event) {
        txtSearch.clear();
        loadAllInventory();
    }

    private InventoryDto getInventoryDtoFromForm() throws IllegalArgumentException {
        try {
            return new InventoryDto(
                    Integer.parseInt(txtInventoryId.getText()),
                    cmbSupplierId.getValue(),
                    txtSupplierName.getText(),
                    Integer.parseInt(txtNewQuantity.getText()),
                    dpLastUpdate.getValue(),
                    txtCategory.getText(),
                    Double.parseDouble(txtPrice.getText())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric values in form");
        }
    }

    private void fillFormWithInventory(InventoryDto dto) {
        txtInventoryId.setText(String.valueOf(dto.getInvId()));
        cmbSupplierId.setValue(dto.getSupId());
        txtSupplierName.setText(dto.getSupplierName());
        txtCurrentQuantity.setText(String.valueOf(dto.getStockQty()));
        txtQuantityAdjustment.clear();
        txtNewQuantity.clear();
        dpLastUpdate.setValue(dto.getLastUpdate());
        txtCategory.setText(dto.getCategory());
        txtPrice.setText(String.valueOf(dto.getPrice()));
        cmbQuantityOperation.getSelectionModel().selectFirst();
    }

    private void clearForm() {
        generateNewInventoryId();
        cmbSupplierId.getSelectionModel().clearSelection();
        txtSupplierName.clear();
        txtCurrentQuantity.clear();
        txtQuantityAdjustment.clear();
        txtNewQuantity.clear();
        dpLastUpdate.setValue(LocalDate.now());
        txtCategory.clear();
        txtPrice.clear();
        cmbQuantityOperation.getSelectionModel().selectFirst();
        tblInventory.getSelectionModel().clearSelection();
        updateButtonStates();
    }

    private void loadAllInventory() {
        try {
            List<InventoryDto> inventoryList = inventoryModel.getAllInventory();
            ObservableList<InventoryDto> list = FXCollections.observableArrayList(inventoryList);
            tblInventory.setItems(list);
        } catch (SQLException e) {
            showDetailedAlert(Alert.AlertType.ERROR, "Database Error",
                    "Error loading inventory", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }

    private void showDetailedAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupTableSelectionListener() {
        tblInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFormWithInventory(newSelection);
            }
            updateButtonStates();
        });
    }

    private void selectMatchingTableRow(InventoryDto dto) {
        tblInventory.getItems().stream()
                .filter(item -> item.getInvId() == dto.getInvId())
                .findFirst()
                .ifPresent(match -> {
                    tblInventory.getSelectionModel().select(match);
                    tblInventory.scrollTo(match);
                });
    }

    public void refreshInventoryOnTabFocus() {
        loadAllInventory();
        clearForm();
    }

    @FXML
    void btnclearField(ActionEvent actionEvent) {
        clearForm();
    }
}