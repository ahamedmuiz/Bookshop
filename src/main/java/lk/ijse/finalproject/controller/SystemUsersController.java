package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import lk.ijse.finalproject.dto.SystemUsersDto;
import lk.ijse.finalproject.model.SystemUsersModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class SystemUsersController {

    @FXML
    private TableColumn<SystemUsersDto, String> colEmail;
    @FXML
    private TableColumn<SystemUsersDto, String> colFirstName;
    @FXML
    private TableColumn<SystemUsersDto, String> colLastName;
    @FXML
    private TableColumn<SystemUsersDto, String> colPassword;
    @FXML
    private TableColumn<SystemUsersDto, Integer> colUserId;
    @FXML
    private TableColumn<SystemUsersDto, String> colUsername;
    @FXML
    private Label lblStatus;
    @FXML
    private VBox mainContent;
    @FXML
    private TableView<SystemUsersDto> tblUsers;
    @FXML
    private TextField txtConfirmPassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtUserId;
    @FXML
    private TextField txtUsername;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public void initialize() {
        setCellValueFactory();
        loadAllUsers();
        makeUserIdNonEditable();
    }

    private void setCellValueFactory() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void makeUserIdNonEditable() {
        txtUserId.setEditable(false);
        txtUserId.setStyle("-fx-background-color: #f0f0f0;");
    }

    private void loadAllUsers() {
        try {
            List<SystemUsersDto> userList = SystemUsersModel.getAllUsers();
            ObservableList<SystemUsersDto> obList = FXCollections.observableArrayList(userList);
            tblUsers.setItems(obList);
            lblStatus.setText("Loaded " + userList.size() + " users");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    void btnClearUserPage(ActionEvent event) {
        clearFields();
        lblStatus.setText("Fields cleared");
    }

    private void clearFields() {
        txtUserId.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
    }

    @FXML
    void btnLoadAllUsers(ActionEvent event) {
        loadAllUsers();
    }

    @FXML
    void btnUserDelete(ActionEvent event) {
        try {
            if (txtUserId.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to delete!");
                return;
            }

            Optional<ButtonType> result = showConfirmationDialog("Delete User",
                    "Are you sure you want to delete this user?");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                int userId = Integer.parseInt(txtUserId.getText());
                boolean isDeleted = SystemUsersModel.deleteUser(userId);

                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                    clearFields();
                    loadAllUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Delete operation failed!");
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID format!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    void btnUserSearch(ActionEvent event) {
        // Create search dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search User");
        dialog.setHeaderText("Enter email or username to search:");

        // Set the button types
        ButtonType searchButton = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButton, ButtonType.CANCEL);

        // Create text field for search input
        TextField searchField = new TextField();
        searchField.setPromptText("Email or Username");
        dialog.getDialogPane().setContent(searchField);

        // Request focus on the field
        searchField.requestFocus();

        // Convert result to string when search button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButton) {
                return searchField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(searchTerm -> {
            try {
                // Try searching by email first
                SystemUsersDto userByEmail = SystemUsersModel.searchUserByEmail(searchTerm);
                if (userByEmail != null) {
                    populateFields(userByEmail);
                    highlightTableRow(userByEmail);
                    lblStatus.setText("User found by email: " + searchTerm);
                    return;
                }

                // If not found by email, try searching by username
                SystemUsersDto userByUsername = SystemUsersModel.searchUserByUsername(searchTerm);
                if (userByUsername != null) {
                    populateFields(userByUsername);
                    highlightTableRow(userByUsername);
                    lblStatus.setText("User found by username: " + searchTerm);
                    return;
                }

                // If not found by either
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No user found with: " + searchTerm);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        });
    }

    private void highlightTableRow(SystemUsersDto user) {
        tblUsers.getSelectionModel().clearSelection();
        for (int i = 0; i < tblUsers.getItems().size(); i++) {
            if (tblUsers.getItems().get(i).getUserId() == user.getUserId()) {
                tblUsers.getSelectionModel().select(i);
                tblUsers.scrollTo(i);
                break;
            }
        }
    }

    @FXML
    void btnUserUpdate(ActionEvent event) {
        try {
            if (txtUserId.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to update!");
                return;
            }

            if (!validateFields()) {
                return;
            }

            if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords don't match!");
                return;
            }

            // Check if any field was actually modified
            SystemUsersDto originalUser = SystemUsersModel.searchUser(Integer.parseInt(txtUserId.getText()));
            SystemUsersDto updatedUser = getUpdatedUserDto();

            if (usersEqual(originalUser, updatedUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "No changes detected. Nothing to update.");
                return;
            }

            Optional<ButtonType> result = showConfirmationDialog("Update User",
                    "Are you sure you want to update this user?");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean isUpdated = SystemUsersModel.updateUser(updatedUser);

                if (isUpdated) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                    clearFields();
                    loadAllUsers();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Update failed!");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID format!");
        }
    }

    private boolean usersEqual(SystemUsersDto original, SystemUsersDto updated) {
        return original.getFirstName().equals(updated.getFirstName()) &&
                original.getLastName().equals(updated.getLastName()) &&
                original.getEmail().equals(updated.getEmail()) &&
                original.getUsername().equals(updated.getUsername()) &&
                original.getPassword().equals(updated.getPassword());
    }

    private SystemUsersDto getUpdatedUserDto() {
        return new SystemUsersDto(
                Integer.parseInt(txtUserId.getText()),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtEmail.getText(),
                txtUsername.getText(),
                txtPassword.getText()
        );
    }

    private boolean validateFields() {
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || txtUsername.getText().isEmpty() ||
                txtPassword.getText().isEmpty() || txtConfirmPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
            return false;
        }

        if (!NAME_PATTERN.matcher(txtFirstName.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid First Name",
                    "First name must:\n- Be 2-50 characters\n- Contain only letters");
            txtFirstName.requestFocus();
            return false;
        }

        if (!NAME_PATTERN.matcher(txtLastName.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Last Name",
                    "Last name must:\n- Be 2-50 characters\n- Contain only letters");
            txtLastName.requestFocus();
            return false;
        }

        if (!EMAIL_PATTERN.matcher(txtEmail.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email",
                    "Please enter a valid email address (e.g., example@domain.com)");
            txtEmail.requestFocus();
            return false;
        }

        if (!USERNAME_PATTERN.matcher(txtUsername.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Username",
                    "Username must:\n- Be 4-20 characters\n- Contain only letters, numbers and underscores");
            txtUsername.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(txtPassword.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Weak Password",
                    "Password must:\n- Be at least 8 characters\n- Contain at least one digit\n- " +
                            "Contain at least one lowercase and uppercase letter\n- " +
                            "Contain at least one special character (@#$%^&+=)");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    @FXML
    void tblUsersOnMouseClicked(javafx.scene.input.MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            SystemUsersDto selectedItem = tblUsers.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                populateFields(selectedItem);
            }
        }
    }

    private void populateFields(SystemUsersDto dto) {
        txtUserId.setText(String.valueOf(dto.getUserId()));
        txtFirstName.setText(dto.getFirstName());
        txtLastName.setText(dto.getLastName());
        txtEmail.setText(dto.getEmail());
        txtUsername.setText(dto.getUsername());
        txtPassword.setText(dto.getPassword());
        txtConfirmPassword.setText(dto.getPassword());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}