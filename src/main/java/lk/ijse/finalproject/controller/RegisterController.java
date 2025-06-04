package lk.ijse.finalproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.finalproject.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField password;

    @FXML
    private Button registerButton;

    @FXML
    private TextField username;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @FXML
    void register(ActionEvent event) {
        String fname = firstName.getText().trim();
        String lname = lastName.getText().trim();
        String userEmail = email.getText().trim();
        String user = username.getText().trim();
        String pass = password.getText();
        String confirmPass = confirmPassword.getText();

        // Validate fields
        if (!validateFields(fname, lname, userEmail, user, pass, confirmPass)) {
            return;
        }

        if (saveUser(fname, lname, userEmail, user, pass)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
            navigateToLogin(event); // Navigate to login page after successful registration
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Try again.");
        }
    }

    private boolean validateFields(String fname, String lname, String email,
                                   String username, String password, String confirmPass) {
        // Check empty fields
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all fields.");
            return false;
        }

        // Validate first name
        if (!NAME_PATTERN.matcher(fname).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid First Name",
                    "First name must:\n- Be 2-50 characters\n- Contain only letters");
            firstName.requestFocus();
            return false;
        }

        // Validate last name
        if (!NAME_PATTERN.matcher(lname).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Last Name",
                    "Last name must:\n- Be 2-50 characters\n- Contain only letters");
            lastName.requestFocus();
            return false;
        }

        // Validate email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email",
                    "Please enter a valid email address (e.g., example@domain.com)");
            this.email.requestFocus();
            return false;
        }

        // Validate username
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Username",
                    "Username must:\n- Be 4-20 characters\n- Contain only letters, numbers and underscores");
            this.username.requestFocus();
            return false;
        }

        // Validate password
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert(Alert.AlertType.ERROR, "Weak Password",
                    "Password must:\n- Be at least 8 characters\n- Contain at least one digit\n- " +
                            "Contain at least one lowercase and uppercase letter\n- " +
                            "Contain at least one special character (@#$%^&+=)");
            this.password.requestFocus();
            return false;
        }

        // Check password match
        if (!password.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            confirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean saveUser(String fname, String lname, String email, String username, String password) {
        String sql = "INSERT INTO users (first_name, last_name, email, username, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setString(3, email);
            pstmt.setString(4, username);
            pstmt.setString(5, password);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Username or email already exists. Please try different ones.");
            return false;
        }
    }

    private void navigateToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent parent = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void showLoginStage(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent parent = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}