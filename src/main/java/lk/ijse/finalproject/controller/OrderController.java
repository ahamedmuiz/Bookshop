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

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    @FXML private ComboBox<String> cmbCustomerId;
    @FXML private ComboBox<String> cmbItemId;
    @FXML private TableView<CartTm> tblCart;
    @FXML private TableColumn<CartTm, String> colItemId;
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
        colItemId.setCellValueFactory(new PropertyValueFactory<>("invId"));  // Changed from "bId" to "invId"
        colItemName.setCellValueFactory(new PropertyValueFactory<>("category"));  // Changed from "name" to "category"
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
    }

    private void resetPage() throws SQLException {
        lblOrderId.setText(String.valueOf(orderModel.getNextOrderId())); // Convert int to String
        lblOrderDate.setText(LocalDate.now().toString());
        loadCustomerIds();
        loadInventoryIds();
        calculateTotal();
    }

    private void loadInventoryIds() throws SQLException {
        cmbItemId.setItems(FXCollections.observableArrayList(bookModel.getAllBookIds()));  // Changed method name
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
        String invId = cmbItemId.getValue();
        if (invId != null) {
            BookDto inventoryItem = bookModel.findById(invId);  // Still using BookDto but for inventory
            if (inventoryItem != null) {
                lblItemName.setText(inventoryItem.getCategory());
                lblItemQty.setText(String.valueOf(inventoryItem.getQty()));
                lblItemPrice.setText(String.valueOf(inventoryItem.getPrice()));
            }
        }
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        String selectedInvId = cmbItemId.getValue();
        String cartQtyString = txtAddToCartQty.getText();

        if (!validateCartAddition(selectedInvId, cartQtyString)) return;

        int cartQty = Integer.parseInt(cartQtyString);
        double unitPrice = Double.parseDouble(lblItemPrice.getText());
        double total = unitPrice * cartQty;

        // Check if item is already in the cart
        CartTm existingItem = null;
        for (CartTm item : cartData) {
            if (item.getInvId().equals(selectedInvId)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            // Calculate total quantity and validate against stock
            int newQuantity = existingItem.getQty() + cartQty;
            int stockQty = Integer.parseInt(lblItemQty.getText());

            if (newQuantity > stockQty) {
                showAlert(Alert.AlertType.WARNING, "Cannot add more than the available stock!");
                return;
            }

            existingItem.setQty(newQuantity);
            existingItem.setTotal(existingItem.getPrice() * newQuantity);
            tblCart.refresh();
        } else {
            // New item to be added
            Button removeBtn = new Button("Remove");
            CartTm cartTm = new CartTm(selectedInvId, lblItemName.getText(), cartQty, unitPrice, total, removeBtn);

            removeBtn.setOnAction(e -> {
                cartData.remove(cartTm);
                calculateTotal();
            });

            cartData.add(cartTm);
        }

        calculateTotal();
        txtAddToCartQty.clear();
    }


    private boolean validateCartAddition(String selectedInvId, String cartQtyString) {
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
            new Alert(Alert.AlertType.WARNING, "Please add items to cart!").show();
            return;
        }

        if (cmbCustomerId.getValue() == null || cmbCustomerId.getValue().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer!").show();
            return;
        }

        String customerId = cmbCustomerId.getValue();
        String orderId = lblOrderId.getText();
        Date date = Date.valueOf(lblOrderDate.getText());

        ArrayList<OrderBookContainDto> cartList = new ArrayList<>();
        for (CartTm cartTm : cartData) {
            cartList.add(new OrderBookContainDto(
                    orderId,
                    cartTm.getInvId(),  // Changed from getBId() to getInvId()
                    cartTm.getQty(),
                    cartTm.getPrice()
            ));
        }

        OrderDto orderDto = new OrderDto(
                orderId,
                customerId,
                date,
                cartList
        );

        try {
            boolean isPlaced = orderModel.placeOrder(orderDto);
            if (isPlaced) {
                new Alert(Alert.AlertType.CONFIRMATION, "Order placed successfully!").show();
                resetPage();
                cartData.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to place order!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to place order!").show();
            e.printStackTrace();
        }
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