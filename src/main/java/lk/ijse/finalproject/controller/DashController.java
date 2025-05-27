package lk.ijse.finalproject.controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashController {

    @FXML
    private AnchorPane ancMainDashSwitch;

    @FXML
    private Button btnAttendance;

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnEmployees;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnSalary;

    @FXML
    private Button btnSales;

    @FXML
    private Button btnSuppliers;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;


    private List<Button> buttonList;

    @FXML
    public void initialize() {
        // Set current date
        dateLabel.setText(LocalDate.now().toString());

        // Set current time and update every second
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // Add all buttons to a list for easy management
        buttonList = new ArrayList<>(List.of(
                btnInventory, btnCustomer, btnOrders,
                btnSales, btnEmployees, btnSuppliers, btnAttendance, btnSalary, btnLogout
        ));

        btnLogout.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
    }


    @FXML
    void handleButtonClick(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();

        // If logout clicked popup confirmation box
        if (clickedButton == btnLogout) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout Confirmation");
            alert.setHeaderText("Are you sure you want to logout?");
            alert.setContentText("Click OK to logout or Cancel to stay.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Parent loginPage = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));
                Scene loginScene = new Scene(loginPage);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(loginScene);
                stage.show();
            }
            return;
        }


        // Reset styles
        for (Button button : buttonList) {
            if (button == btnLogout) {
                button.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
            } else {
                button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white;");
            }
        }

        // clicked button become yellow in dashboard

        clickedButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: bold;");

        // Load UI
        if (clickedButton == btnInventory) {
            loadUI("InventoryPage");
        }
        else if (clickedButton == btnCustomer) {
            loadUI("CustomerPage");
        }
        else if (clickedButton == btnOrders) {
            loadUI("OrderPage");
        }
        else if (clickedButton == btnSales) {
            loadUI("SalePage");
        }
        else if (clickedButton == btnEmployees) {
            loadUI("EmployeePage");
        }
        else if (clickedButton == btnSuppliers) {
            loadUI("SupplierPage");
        }
        else if (clickedButton == btnAttendance) {
            loadUI("AttendancePage");
        }
        else if (clickedButton == btnSalary) {
            loadUI("SalaryPage");
        }

    }


    private void loadUI(String InventoryPage) {
        try {
            Parent node = FXMLLoader.load(getClass().getResource("/view/" + InventoryPage + ".fxml"));
            ancMainDashSwitch.getChildren().clear();
            ancMainDashSwitch.getChildren().add(node);

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}