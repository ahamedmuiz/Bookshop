package lk.ijse.finalproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.finalproject.dto.BookDto;
import lk.ijse.finalproject.dto.OrderBookContainDto;
import lk.ijse.finalproject.dto.OrderDto;
import lk.ijse.finalproject.dto.tm.CartTm;
import lk.ijse.finalproject.model.BookModel;
import lk.ijse.finalproject.model.CustomerModel;
import lk.ijse.finalproject.model.InventoryModel;
import lk.ijse.finalproject.model.OrderModel;
import lk.ijse.finalproject.util.PaymentDialog;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private ComboBox<String> cmbCustomerId;
    @FXML private ComboBox<Integer> cmbItemId;  // Changed to Integer type
    @FXML private TableView<CartTm> tblCart;
    @FXML private TableColumn<CartTm, Integer> colItemId;  // Changed to Integer type
    @FXML private TableColumn<CartTm, String> colItemName;
    @FXML private TableColumn<CartTm, Double> colUnitPrice;
    @FXML private TableColumn<CartTm, Integer> colQuantity;
    @FXML private TableColumn<CartTm, Double> colTotal;
    @FXML private TableColumn<CartTm, Button> colAction;
    @FXML private Label lblCustomerName, lblItemName, lblItemPrice, lblItemQty, lblOrderId, lblOrderDate, lblTotal;
    @FXML private TextField txtAddToCartQty;

    private final OrderModel orderModel = new OrderModel();
    private final CustomerModel customerModel = new CustomerModel();
    private final BookModel bookModel = new BookModel();
    private final InventoryModel inventoryModel = new InventoryModel();

    private final ObservableList<CartTm> cartData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        tblCart.setItems(cartData);

        try {
            resetPage();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load data!");
            e.printStackTrace();
        }
    }

    private void initializeTableColumns() {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("invId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
    }

    private void resetPage() throws SQLException {
        lblOrderId.setText(String.valueOf(orderModel.getNextOrderId()));
        lblOrderDate.setText(LocalDate.now().toString());
        loadCustomerIds();
        loadInventoryIds();
        calculateTotal();
    }

    private void loadInventoryIds() throws SQLException {

        List<Integer> inventoryIds = bookModel.getAllBookIds();
        cmbItemId.setItems(FXCollections.observableArrayList(inventoryIds));
    }

    private void loadCustomerIds() throws SQLException {
        cmbCustomerId.setItems(FXCollections.observableArrayList(customerModel.getAllCustomerIds()));
    }

    @FXML
    void cmbCustomerOnAction(ActionEvent event) throws SQLException {
        String customerId = cmbCustomerId.getValue();
        if (customerId != null) {
            lblCustomerName.setText(customerModel.findNameById(customerId));
        }
    }

    @FXML
    void cmbItemOnAction(ActionEvent event) throws SQLException {
        Integer invId = cmbItemId.getValue();
        if (invId != null) {
            BookDto inventoryItem = bookModel.findById(invId);
            if (inventoryItem != null) {
                lblItemName.setText(inventoryItem.getCategory());
                lblItemQty.setText(String.valueOf(inventoryItem.getQty()));
                lblItemPrice.setText(String.valueOf(inventoryItem.getPrice()));
            }
        }
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        Integer selectedInvId = cmbItemId.getValue();
        String cartQtyString = txtAddToCartQty.getText();

        if (!validateCartAddition(selectedInvId, cartQtyString)) return;

        int cartQty = Integer.parseInt(cartQtyString);
        double unitPrice = Double.parseDouble(lblItemPrice.getText());
        double total = unitPrice * cartQty;

        CartTm existingItem = findCartItem(selectedInvId);

        if (existingItem != null) {
            updateExistingCartItem(existingItem, cartQty);
        } else {
            addNewCartItem(String.valueOf(selectedInvId), cartQty, unitPrice, total);
        }

        calculateTotal();
        txtAddToCartQty.clear();
    }

    private CartTm findCartItem(Integer invId) {
        for (CartTm item : cartData) {
            if (item.getInvId().equals(invId)) {
                return item;
            }
        }
        return null;
    }

    private void updateExistingCartItem(CartTm existingItem, int additionalQty) {
        int newQuantity = existingItem.getQty() + additionalQty;
        int stockQty = Integer.parseInt(lblItemQty.getText());

        if (newQuantity > stockQty) {
            showAlert(Alert.AlertType.WARNING, "Cannot add more than the available stock!");
            return;
        }

        existingItem.setQty(newQuantity);
        existingItem.setTotal(existingItem.getPrice() * newQuantity);
        tblCart.refresh();
    }

    private void addNewCartItem(String invId, int qty, double price, double total) {
        Button removeBtn = new Button("Remove");
        CartTm cartTm = new CartTm(invId, lblItemName.getText(), qty, price, total, removeBtn);

        removeBtn.setOnAction(e -> {
            cartData.remove(cartTm);
            calculateTotal();
        });

        cartData.add(cartTm);
    }

    private boolean validateCartAddition(Integer selectedInvId, String cartQtyString) {
        if (selectedInvId == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an inventory item!");
            return false;
        }
        if (!cartQtyString.matches("^[1-9][0-9]*$")) {
            showAlert(Alert.AlertType.WARNING, "Please enter a valid quantity!");
            return false;
        }
        int cartQty = Integer.parseInt(cartQtyString);
        int stockQty = Integer.parseInt(lblItemQty.getText());
        if (cartQty > stockQty) {
            showAlert(Alert.AlertType.WARNING, "Not enough stock available!");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).show();
    }

    private void calculateTotal() {
        double total = cartData.stream().mapToDouble(CartTm::getTotal).sum();
        lblTotal.setText(String.format("%.2f", total));
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        if (tblCart.getItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please add items to cart!");
            return;
        }

        if (cmbCustomerId.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a customer!");
            return;
        }

        String paymentMethod = PaymentDialog.showPaymentDialog();
        if (paymentMethod == null) {
            showAlert(Alert.AlertType.INFORMATION, "Order cancelled");
            return;
        }

        String orderId = lblOrderId.getText();
        String customerId = cmbCustomerId.getValue();
        Date date = Date.valueOf(lblOrderDate.getText());
        double total = Double.parseDouble(lblTotal.getText());

        ArrayList<OrderBookContainDto> cartList = getOrderDetailsList(orderId);

        OrderDto orderDto = new OrderDto(
                orderId,
                customerId,
                date,
                total,
                paymentMethod,
                cartList
        );

        try {
            boolean isPlaced = orderModel.placeOrder(orderDto);
            if (isPlaced) {
                showAlert(Alert.AlertType.CONFIRMATION, "Order placed successfully!");
                resetPage();
                cartData.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to place order!");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ArrayList<OrderBookContainDto> getOrderDetailsList(String orderId) {
        ArrayList<OrderBookContainDto> list = new ArrayList<>();
        for (CartTm tm : cartData) {
            list.add(new OrderBookContainDto(
                    orderId,
                    Integer.parseInt(tm.getInvId()),
                    tm.getQty(),
                    tm.getPrice()
            ));
        }
        return list;
    }

    @FXML
    void btnResetOnAction(ActionEvent event) {
        cartData.clear();
        txtAddToCartQty.clear();
        lblCustomerName.setText("");
        lblItemName.setText("");
        lblItemPrice.setText("");
        lblItemQty.setText("");
        calculateTotal();
    }
}