package lk.ijse.finalproject.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lk.ijse.finalproject.dto.SaleAnalysisDto;
import lk.ijse.finalproject.dto.tm.SaleAnalysisTm;
import lk.ijse.finalproject.model.SaleAnalysisModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class SaleAnalysisController {

    @FXML private TableColumn<SaleAnalysisTm, Button> colActions;
    @FXML private TableColumn<SaleAnalysisTm, Double> colAmount;
    @FXML private TableColumn<SaleAnalysisTm, String> colCustomer;
    @FXML private TableColumn<SaleAnalysisTm, String> colCustomerId;
    @FXML private TableColumn<SaleAnalysisTm, String> colOrderDate;
    @FXML private TableColumn<SaleAnalysisTm, String> colOrderId;
    @FXML private TableColumn<SaleAnalysisTm, String> colPayment;
    @FXML private TableColumn<SaleAnalysisTm, String> colStatus;
    @FXML private Label lblLastUpdated;
    @FXML private RadioButton rbAll;
    @FXML private RadioButton rbCancelled;
    @FXML private RadioButton rbCompleted;
    @FXML private RadioButton rbPending;
    @FXML private TableView<SaleAnalysisTm> tblOrders;
    @FXML private TextField txtOrderSearch;
    @FXML private ToggleGroup statusGroup;
    @FXML private ToggleGroup timePeriodGroup;
    @FXML private RadioButton rbToday;
    @FXML private RadioButton rbWeek;
    @FXML private RadioButton rbMonth;
    @FXML private RadioButton rbYear;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Button btnSearch;
    @FXML private Button btnReset;
    @FXML private Label lblTotalSales;

    public void initialize() {
        initializeToggleGroups();
        setupTableColumns();
        initializeDateTimeDisplay();
        loadAllOrders();
    }

    private void initializeToggleGroups() {
        statusGroup = new ToggleGroup();
        rbAll.setToggleGroup(statusGroup);
        rbPending.setToggleGroup(statusGroup);
        rbCompleted.setToggleGroup(statusGroup);
        rbCancelled.setToggleGroup(statusGroup);
        rbAll.setSelected(true);

        timePeriodGroup = new ToggleGroup();
        rbToday.setToggleGroup(timePeriodGroup);
        rbWeek.setToggleGroup(timePeriodGroup);
        rbMonth.setToggleGroup(timePeriodGroup);
        rbYear.setToggleGroup(timePeriodGroup);
    }

    private void setupTableColumns() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("payment"));
        colActions.setCellValueFactory(new PropertyValueFactory<>("btnAction"));

        setupStatusCellFactory();
        setupPaymentCellFactory();
    }

    private void initializeDateTimeDisplay() {
        updateDateTimeLabels();

        Timeline clock = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        e -> updateDateTimeLabels()
                ));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void loadAllOrders() {
        try {
            List<SaleAnalysisDto> orders = SaleAnalysisModel.getAllOrders();
            displayOrders(orders);
            updateTotalSales(orders);
        } catch (SQLException e) {
            showErrorAlert("Failed to load orders: " + e.getMessage());
        }
    }

    private void displayOrders(List<SaleAnalysisDto> orders) {
        ObservableList<SaleAnalysisTm> obList = FXCollections.observableArrayList();

        for (SaleAnalysisDto dto : orders) {
            HBox buttonContainer = createActionButtons(dto);

            obList.add(new SaleAnalysisTm(
                    dto.getOrderId(),
                    dto.getCustomerId(),
                    dto.getCustomerName(),
                    dto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    dto.getAmount(),
                    dto.getStatus(),
                    dto.getPaymentMethod(),
                    buttonContainer
            ));
        }
        tblOrders.setItems(obList);
    }

    private HBox createActionButtons(SaleAnalysisDto dto) {
        HBox buttonContainer = new HBox(5);

        switch (dto.getStatus()) {
            case "Pending":
                buttonContainer.getChildren().addAll(
                        createButton("Process", "#3498db", e -> processOrder(dto.getOrderId())),
                        createButton("Cancel", "#e74c3c", e -> cancelOrder(dto.getOrderId()))
                );
                break;
            case "Processing":
                buttonContainer.getChildren().add(
                        createButton("Complete", "#2ecc71", e -> completeOrder(dto.getOrderId()))
                );
                break;
            default:
                buttonContainer.getChildren().add(
                        createButton("View", "#95a5a6", e -> viewOrderDetails(dto.getOrderId()))
                );
        }
        return buttonContainer;
    }

    private Button createButton(String text, String color, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setOnAction(handler);
        return button;
    }

    private void updateTotalSales(List<SaleAnalysisDto> orders) {
        double total = orders.stream()
                .mapToDouble(SaleAnalysisDto::getAmount)
                .sum();
        lblTotalSales.setText(String.format("Total Sales: LKR %,.2f", total));
    }

    private void updateDateTimeLabels() {
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblLastUpdated.setText("Last updated: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @FXML
    private void btnSearchOnAction(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void btnResetOnAction(ActionEvent event) {
        statusGroup.selectToggle(rbAll);
        timePeriodGroup.selectToggle(null);
        txtOrderSearch.clear();
        loadAllOrders();
    }

    private void applyFilters() {
        try {
            String status = getSelectedStatus();
            String period = getSelectedTimePeriod();
            String searchText = txtOrderSearch.getText().trim();

            List<SaleAnalysisDto> filteredOrders;

            if (!searchText.isEmpty()) {
                filteredOrders = SaleAnalysisModel.searchOrders(searchText);

                if (!"All".equals(status)) {
                    filteredOrders.removeIf(dto -> !dto.getStatus().equals(status));
                }

                if (period != null) {
                    filteredOrders.removeIf(dto -> !isInTimePeriod(dto.getDate(), period));
                }
            } else {
                if (period != null) {
                    filteredOrders = SaleAnalysisModel.filterOrdersByTimePeriod(period);

                    if (!"All".equals(status)) {
                        filteredOrders.removeIf(dto -> !dto.getStatus().equals(status));
                    }
                } else if (!"All".equals(status)) {
                    filteredOrders = SaleAnalysisModel.filterOrdersByStatus(status);
                } else {
                    filteredOrders = SaleAnalysisModel.getAllOrders();
                }
            }

            displayOrders(filteredOrders);
            updateTotalSales(filteredOrders);
            updateDateTimeLabels();
        } catch (SQLException e) {
            showErrorAlert("Error applying filters: " + e.getMessage());
        }
    }

    private void processOrder(String orderId) {
        updateOrderStatus(orderId, "Processing");
    }

    private void completeOrder(String orderId) {
        updateOrderStatus(orderId, "Completed");
    }

    private void cancelOrder(String orderId) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel order #" + orderId + "? Inventory will be restored.",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean success = SaleAnalysisModel.cancelOrderAndRestoreInventory(orderId);
                if (success) {
                    showInfoAlert("Order #" + orderId + " cancelled successfully!\nInventory quantities restored.");
                    applyFilters();
                } else {
                    showErrorAlert("Failed to cancel order #" + orderId);
                }
            } catch (SQLException e) {
                showErrorAlert("Error cancelling order: " + e.getMessage());
            }
        }
    }

    private void updateOrderStatus(String orderId, String newStatus) {
        try {
            boolean success = SaleAnalysisModel.updateOrderStatus(orderId, newStatus);
            if (success) {
                showInfoAlert("Order status updated to " + newStatus);
                applyFilters();
            } else {
                showErrorAlert("Failed to update order status");
            }
        } catch (SQLException e) {
            showErrorAlert("Error updating status: " + e.getMessage());
        }
    }

    private void viewOrderDetails(String orderId) {
        showInfoAlert("Viewing details for order: " + orderId);
    }

    private String getSelectedStatus() {
        RadioButton selected = (RadioButton) statusGroup.getSelectedToggle();
        return selected != null ? selected.getText() : "All";
    }

    private String getSelectedTimePeriod() {
        RadioButton selected = (RadioButton) timePeriodGroup.getSelectedToggle();
        return selected != null ? selected.getText().replace(" ", "").toLowerCase() : null;
    }

    private boolean isInTimePeriod(LocalDate date, String period) {
        LocalDate now = LocalDate.now();
        switch (period) {
            case "today": return date.isEqual(now);
            case "thisweek":
                return date.getYear() == now.getYear() &&
                        date.getMonth() == now.getMonth() &&
                        date.getDayOfWeek() == now.getDayOfWeek();
            case "thismonth":
                return date.getYear() == now.getYear() &&
                        date.getMonth() == now.getMonth();
            case "thisyear":
                return date.getYear() == now.getYear();
            default: return true;
        }
    }

    private void setupStatusCellFactory() {
        colStatus.setCellFactory(column -> new TableCell<SaleAnalysisTm, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Pending": setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); break;
                        case "Processing": setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;"); break;
                        case "Completed": setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); break;
                        case "Cancelled": setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); break;
                        default: setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupPaymentCellFactory() {
        colPayment.setCellFactory(column -> new TableCell<SaleAnalysisTm, String>() {
            @Override
            protected void updateItem(String payment, boolean empty) {
                super.updateItem(payment, empty);
                if (empty || payment == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(payment);
                    if ("Cash".equalsIgnoreCase(payment)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if ("Card".equalsIgnoreCase(payment)) {
                        setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void showErrorAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    private void showInfoAlert(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }
}