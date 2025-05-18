package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.OrderBookContainDto;
import lk.ijse.finalproject.dto.OrderDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderModel {
    private final OrderBookContainModel orderBookContainModel = new OrderBookContainModel();

    /**
     * Generates the next Order ID in the format "O001", "O002", etc.
     */
    public String getNextOrderId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute(
                "SELECT O_ID FROM OrderTable ORDER BY O_ID DESC LIMIT 1"
        );

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            // Remove the "O" prefix and parse the number
            int lastIdNumber = Integer.parseInt(lastId.substring(1));
            // Increment the ID number and format back with "O" prefix
            return String.format("O%03d", lastIdNumber + 1);
        }
        return "O001";
    }

    /**
     * Places an order and saves it to the database, along with its details.
     */
    public boolean placeOrder(OrderDto orderDto) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            // Save order with payment method
            boolean isOrderSaved = CrudUtil.execute(
                    "INSERT INTO OrderTable (O_ID, C_ID, Date, Total_Amount, Payment_Method) VALUES (?, ?, ?, ?, ?)",
                    orderDto.getOId(),
                    orderDto.getCId(),
                    orderDto.getDate(),
                    orderDto.getTotalAmount(),
                    orderDto.getPaymentMethod()
            );

            if (isOrderSaved) {
                // Save order items
                boolean isItemsSaved = orderBookContainModel.saveOrderDetailsList(orderDto.getCartList());

                if (isItemsSaved) {
                    // Update inventory
                    boolean isInventoryUpdated = updateInventory(orderDto.getCartList());

                    if (isInventoryUpdated) {
                        connection.commit();
                        return true;
                    }
                }
            }

            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private boolean updateInventory(ArrayList<OrderBookContainDto> cartList) throws SQLException {
        for (OrderBookContainDto item : cartList) {
            boolean updated = CrudUtil.execute(
                    "UPDATE Inventory SET Stock_Qty = Stock_Qty - ? WHERE Inv_ID = ?",
                    item.getQty(),
                    item.getInvId()
            );
            if (!updated) return false;
        }
        return true;
    }
}