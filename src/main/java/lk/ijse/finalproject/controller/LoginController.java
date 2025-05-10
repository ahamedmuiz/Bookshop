package lk.ijse.finalproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.finalproject.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private PasswordField txtpassword;

    @FXML
    private TextField txtusername;

    @FXML
    void btnGoToDashboard(ActionEvent event) throws IOException {
        String inputUserName = txtusername.getText();
        String inputPassword = txtpassword.getText();

        if (inputUserName.isEmpty() || inputPassword.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill in both fields.").show();
            return;
        }

        if (checkCredentials(inputUserName, inputPassword)) {
            Stage stage = (Stage) txtusername.getScene().getWindow();
            Parent parent = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Wrong username or password").show();
        }
    }

    private boolean checkCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    void txtGoToRegistration (MouseEvent event) throws IOException {
        Stage stage = (Stage) txtusername.getScene().getWindow();
        Parent parent = FXMLLoader.load(getClass().getResource("/view/RegisterPage.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }


}
