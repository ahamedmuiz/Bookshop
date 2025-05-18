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
    private Button btnDelivery;

    @FXML
    private Button btnEmployees;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnPayments;

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
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Set current time and update every second
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // Add all buttons to a list for easy management
        buttonList = new ArrayList<>(List.of(
                btnInventory, btnCustomer, btnOrders, btnPayments, btnDelivery,
                btnSales, btnEmployees, btnSuppliers, btnAttendance, btnSalary, btnLogout
        ));

        btnLogout.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
    }

    @FXML
    void handleButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        // If logout clicked popup confirmation box
        if (clickedButton == btnLogout) {
            handleLogout(event);
            return;
        }

        // Reset styles
        resetButtonStyles();

        // Set clicked button style
        clickedButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: bold;");

        // Load UI based on button clicked
        try {
            String fxmlFile = getFxmlPathForButton(clickedButton);
            loadUI(fxmlFile);
        } catch (Exception e) {
            showErrorAlert("Failed to load view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Click OK to logout or Cancel to stay.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Parent loginPage = FXMLLoader.load(getClass().getResource("/lk/ijse/finalproject/view/LoginPage.fxml"));
                Scene loginScene = new Scene(loginPage);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(loginScene);
                stage.show();
            } catch (IOException e) {
                showErrorAlert("Failed to load login page: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void resetButtonStyles() {
        for (Button button : buttonList) {
            if (button == btnLogout) {
                button.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
            } else {
                button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white;");
            }
        }
    }

    private String getFxmlPathForButton(Button button) {
        if (button == btnInventory) return "InventoryPage";
        if (button == btnCustomer) return "CustomerPage";
        if (button == btnOrders) return "OrderPage";
        if (button == btnSuppliers) return "SupplierPage";
        if (button == btnEmployees) return "EmployeePage";
        if (button == btnAttendance) return "AttendancePage";
        if (button == btnSalary) return "SalaryPage";
        if (button == btnSales) return "SalePage";
        if (button == btnPayments) return "PaymentPage";
        if (button == btnDelivery) return "DeliveryPage";
        throw new IllegalArgumentException("No FXML mapping for button: " + button.getId());
    }

    private void loadUI(String fxmlFileName) throws IOException {
        String fxmlPath = "/view/" + fxmlFileName + ".fxml";
        System.out.println("Attempting to load: " + fxmlPath); // Debug line

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found at: " + fxmlPath);
        }

        Parent node = loader.load();
        ancMainDashSwitch.getChildren().clear();
        ancMainDashSwitch.getChildren().add(node);

        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}