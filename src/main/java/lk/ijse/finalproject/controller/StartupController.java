package lk.ijse.finalproject.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class StartupController {


    public void btnGoToLoginPage(ActionEvent event) throws IOException {
        // Load loginpage.fxml
        try {
            Parent loginPage = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));
            Scene loginScene = new Scene(loginPage);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load LoginPage.fxml. Please check the path.");
        }

    }
}
