package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.OrderDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            // Calculate the total amount
            double totalAmount = orderDto.getCartList().stream()
                    .mapToDouble(dto -> dto.getPrice() * dto.getQty())
                    .sum();

            // Execute the order insertion with the correct parameters
            boolean isSaved = CrudUtil.execute(
                    "INSERT INTO OrderTable (O_ID, C_ID, Date, Total_Amount) VALUES (?, ?, ?, ?)",
                    orderDto.getOId(),      // Example: "O001"
                    orderDto.getCId(),
                    orderDto.getDate(),
                    totalAmount
            );

            if (isSaved) {
                // Save all order details in OrderBookContain
                boolean isDetailsSaved = orderBookContainModel.saveOrderDetailsList(orderDto.getCartList());

                if (isDetailsSaved) {
                    connection.commit();  // Commit if everything is successful
                    return true;
                }
            }

            connection.rollback();  // Rollback if any failure occurs
            return false;
        } catch (Exception e) {
            connection.rollback(); // Ensure rollback on exception
            e.printStackTrace();
            return false;
        } finally {
            connection.setAutoCommit(true);  // Restore auto-commit mode
        }
    }
}
