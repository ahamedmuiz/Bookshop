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

    @FXML
    void register(ActionEvent event) {
        String fname = firstName.getText();
        String lname = lastName.getText();
        String userEmail = email.getText();
        String user = username.getText();
        String pass = password.getText();
        String confirmPass = confirmPassword.getText();

        if (fname.isEmpty() || lname.isEmpty() || userEmail.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all fields.");
            return;
        }

        if (!pass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch!", "Passwords do not match.");
            return;
        }

        if (saveUser(fname, lname, userEmail, user, pass)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Try again.");
        }
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
            return false;
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
