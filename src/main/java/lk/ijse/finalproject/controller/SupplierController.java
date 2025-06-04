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
import java.util.Optional;
import java.util.regex.Pattern;

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
    private SupplierDto currentSupplier;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{3,50}$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[a-zA-Z0-9 ,.-]{5,100}$");

    public void initialize() {
        setCellValueFactories();
        generateNextSupplierId();
        loadAllSuppliers();
        loadSupplierIds();

        // Set up table row selection listener
        tblSupplier.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setSupplierData(newSelection);
                currentSupplier = newSelection;
                loadInventoryForSupplier(newSelection.getSupId());
                cmbSupplierId.setValue(newSelection.getSupId()); // Update combobox selection
            }
        });

        cmbSupplierId.setOnAction(e -> {
            Integer selectedId = cmbSupplierId.getValue();
            if (selectedId != null) {
                try {
                    SupplierDto dto = supplierModel.searchSupplier(selectedId);
                    if (dto != null) {
                        setSupplierData(dto);
                        currentSupplier = dto;
                        loadInventoryForSupplier(selectedId);
                        // Select the row in the table
                        tblSupplier.getSelectionModel().select(dto);
                    }
                } catch (SQLException ex) {
                    showAlert("Database Error", ex.getMessage());
                }
            }
        });
    }


    private void setSupplierData(SupplierDto dto) {
        txtSupplierId.setText(String.valueOf(dto.getSupId()));
        txtName.setText(dto.getName());
        txtContact.setText(dto.getContact());
        txtAddress.setText(dto.getAddress());
    }

    private void generateNextSupplierId() {
        try {
            int nextId = supplierModel.generateNextSupplierId();
            txtSupplierId.setText(String.valueOf(nextId));
            txtSupplierId.setEditable(false);
        } catch (SQLException e) {
            showAlert("Error", "Failed to generate supplier ID: " + e.getMessage());
        }
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
            showAlert("Database Error", "Failed to load suppliers: " + e.getMessage());
        }
    }

    private void loadSupplierIds() {
        try {
            List<Integer> ids = supplierModel.getAllSupplierIds();
            cmbSupplierId.setItems(FXCollections.observableArrayList(ids));
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load supplier IDs: " + e.getMessage());
        }
    }

    private void loadInventoryForSupplier(int supId) {
        try {
            List<InventoryDto> list = inventoryModel.getInventoryBySupplierId(supId);
            tblInventory.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load inventory: " + e.getMessage());
        }
    }

    @FXML
    void btnSaveOnAction() {
        if (!validateInputs()) return;

        try {
            SupplierDto dto = getSupplierDtoFromForm();

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Save");
            confirmAlert.setHeaderText("Save Supplier");
            confirmAlert.setContentText("Are you sure you want to save this supplier?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean saved = supplierModel.addSupplier(dto);
                if (saved) {
                    showAlert("Success", "Supplier saved successfully!");
                    refresh();
                } else {
                    showAlert("Error", "Failed to save supplier!");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    void btnUpdateOnAction() {
        if (currentSupplier == null) {
            showAlert("Warning", "Please select a supplier to update");
            return;
        }

        if (!validateInputs()) return;

        try {
            SupplierDto dto = getSupplierDtoFromForm();

            if (!isSupplierModified(dto)) {
                showAlert("Information", "No changes detected");
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Update");
            confirmAlert.setHeaderText("Update Supplier");
            confirmAlert.setContentText("Are you sure you want to update this supplier?");
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean updated = supplierModel.updateSupplier(dto);
                if (updated) {
                    showAlert("Success", "Supplier updated successfully!");
                    refresh();
                } else {
                    showAlert("Error", "Failed to update supplier!");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    private boolean isSupplierModified(SupplierDto newDto) {
        return !(currentSupplier.getSupId() == newDto.getSupId() &&
                currentSupplier.getName().equals(newDto.getName()) &&
                currentSupplier.getContact().equals(newDto.getContact()) &&
                currentSupplier.getAddress().equals(newDto.getAddress()));
    }

    @FXML
    void btnDeleteOnAction() {
        if (currentSupplier == null) {
            showAlert("Warning", "Please select a supplier to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Supplier");
        confirmAlert.setContentText("Are you sure you want to delete this supplier?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = supplierModel.deleteSupplier(currentSupplier.getSupId());
                if (deleted) {
                    showAlert("Success", "Supplier deleted successfully!");
                    refresh();
                } else {
                    showAlert("Error", "Failed to delete supplier!");
                }
            } catch (SQLException e) {
                showAlert("Database Error", e.getMessage());
            }
        }
    }

    @FXML
    void btnSearchOnAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Supplier");
        dialog.setHeaderText("Search by ID or Name");
        dialog.setContentText("Enter ID or Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(searchTerm -> {
            try {
                List<SupplierDto> searchResults = searchSuppliers(searchTerm);
                if (searchResults.isEmpty()) {
                    showAlert("Information", "No suppliers found matching: " + searchTerm);
                } else {
                    setSupplierData(searchResults.get(0));
                    currentSupplier = searchResults.get(0);
                    cmbSupplierId.setValue(currentSupplier.getSupId());
                    tblSupplier.setItems(FXCollections.observableArrayList(searchResults));
                }
            } catch (SQLException e) {
                showAlert("Database Error", e.getMessage());
            }
        });
    }

    private List<SupplierDto> searchSuppliers(String searchTerm) throws SQLException {
        try {
            // Try to parse as ID first
            int id = Integer.parseInt(searchTerm);
            SupplierDto dto = supplierModel.searchSupplier(id);
            return dto != null ? List.of(dto) : List.of();
        } catch (NumberFormatException e) {
            // Search by name if not a number
            return supplierModel.getAllSuppliers().stream()
                    .filter(s -> s.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .toList();
        }
    }

    @FXML
    void btnClearOnAction() {
        clearFields();
        generateNextSupplierId();
        currentSupplier = null;
    }

    private SupplierDto getSupplierDtoFromForm() {
        return new SupplierDto(
                Integer.parseInt(txtSupplierId.getText()),
                txtName.getText(),
                txtContact.getText(),
                txtAddress.getText()
        );
    }

    private boolean validateInputs() {
        // Validate Name
        if (txtName.getText().isEmpty() || !NAME_PATTERN.matcher(txtName.getText()).matches()) {
            showAlert("Validation Error", "Invalid Name!\n" +
                    "- Must be 3-50 characters\n" +
                    "- Only letters and spaces allowed");
            txtName.requestFocus();
            return false;
        }

        // Validate Contact
        if (txtContact.getText().isEmpty() || !CONTACT_PATTERN.matcher(txtContact.getText()).matches()) {
            showAlert("Validation Error", "Invalid Contact Number!\n" +
                    "- Must be 10 digits\n" +
                    "- Must start with 0 (e.g., 0771234567)");
            txtContact.requestFocus();
            return false;
        }

        // Validate Address
        if (txtAddress.getText().isEmpty() || !ADDRESS_PATTERN.matcher(txtAddress.getText()).matches()) {
            showAlert("Validation Error", "Invalid Address!\n" +
                    "- Must be 5-100 characters\n" +
                    "- Can contain letters, numbers, spaces, commas, dots and hyphens");
            txtAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
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
        generateNextSupplierId();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}