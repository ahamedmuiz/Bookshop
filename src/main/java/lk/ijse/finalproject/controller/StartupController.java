package lk.ijse.finalproject.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class StartupController {


    public void btnGoToLoginPage(ActionEvent event) throws IOException {
        // Load loginpage.fxml
        Parent loginPage = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));

        Scene loginScene = new Scene(loginPage);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}
