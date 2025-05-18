package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.SaleAnalysisDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleAnalysisModel {
    public static List<SaleAnalysisDto> getAllOrders() throws SQLException {
        String sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID ORDER BY o.Date DESC";
        ResultSet rs = CrudUtil.execute(sql);

        List<SaleAnalysisDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SaleAnalysisDto(
                    rs.getString("O_ID"),
                    rs.getString("C_ID"),
                    rs.getString("Name"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getDouble("Total_Amount"),
                    rs.getString("Status"),
                    rs.getString("Payment_Method")
            ));
        }
        return list;
    }

    public static List<SaleAnalysisDto> filterOrdersByStatus(String status) throws SQLException {
        String sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                "WHERE o.Status = ? ORDER BY o.Date DESC";
        ResultSet rs = CrudUtil.execute(sql, status);

        List<SaleAnalysisDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SaleAnalysisDto(
                    rs.getString("O_ID"),
                    rs.getString("C_ID"),
                    rs.getString("Name"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getDouble("Total_Amount"),
                    rs.getString("Status"),
                    rs.getString("Payment_Method")
            ));
        }
        return list;
    }

    public static List<SaleAnalysisDto> filterOrdersByTimePeriod(String period) throws SQLException {
        String sql;
        switch (period.toLowerCase()) {
            case "today":
                sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                        "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                        "WHERE DATE(o.Date) = CURDATE() ORDER BY o.Date DESC";
                break;
            case "week":
                sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                        "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                        "WHERE YEARWEEK(o.Date, 1) = YEARWEEK(CURDATE(), 1) ORDER BY o.Date DESC";
                break;
            case "month":
                sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                        "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                        "WHERE YEAR(o.Date) = YEAR(CURDATE()) AND MONTH(o.Date) = MONTH(CURDATE()) ORDER BY o.Date DESC";
                break;
            case "year":
                sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                        "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                        "WHERE YEAR(o.Date) = YEAR(CURDATE()) ORDER BY o.Date DESC";
                break;
            default:
                return getAllOrders();
        }

        ResultSet rs = CrudUtil.execute(sql);
        List<SaleAnalysisDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SaleAnalysisDto(
                    rs.getString("O_ID"),
                    rs.getString("C_ID"),
                    rs.getString("Name"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getDouble("Total_Amount"),
                    rs.getString("Status"),
                    rs.getString("Payment_Method")
            ));
        }
        return list;
    }

    public static List<SaleAnalysisDto> searchOrders(String searchText) throws SQLException {
        String sql = "SELECT o.O_ID, c.C_ID, c.Name, o.Date, o.Total_Amount, o.Status, o.Payment_Method " +
                "FROM OrderTable o JOIN Customer c ON o.C_ID = c.C_ID " +
                "WHERE o.O_ID LIKE ? OR c.C_ID LIKE ? OR c.Name LIKE ? OR o.Status LIKE ? " +
                "OR o.Payment_Method LIKE ? OR CAST(o.Date AS CHAR) LIKE ? " +
                "OR CAST(o.Total_Amount AS CHAR) LIKE ? " +
                "ORDER BY o.Date DESC";

        String searchPattern = "%" + searchText + "%";
        ResultSet rs = CrudUtil.execute(sql,
                searchPattern, searchPattern, searchPattern,
                searchPattern, searchPattern, searchPattern, searchPattern);

        List<SaleAnalysisDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SaleAnalysisDto(
                    rs.getString("O_ID"),
                    rs.getString("C_ID"),
                    rs.getString("Name"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getDouble("Total_Amount"),
                    rs.getString("Status"),
                    rs.getString("Payment_Method")
            ));
        }
        return list;
    }

    public static boolean updateOrderStatus(String orderId, String newStatus) throws SQLException {
        String sql = "UPDATE OrderTable SET Status = ? WHERE O_ID = ?";
        return CrudUtil.execute(sql, newStatus, orderId);
    }

    public static double getTotalSalesByStatus(String status) throws SQLException {
        String sql = "SELECT SUM(Total_Amount) FROM OrderTable WHERE Status = ?";
        ResultSet rs = CrudUtil.execute(sql, status);
        return rs.next() ? rs.getDouble(1) : 0.0;
    }

    public static double getTotalSalesByTimePeriod(String period) throws SQLException {
        String sql;
        switch (period.toLowerCase()) {
            case "today":
                sql = "SELECT SUM(Total_Amount) FROM OrderTable WHERE DATE(Date) = CURDATE()";
                break;
            case "week":
                sql = "SELECT SUM(Total_Amount) FROM OrderTable WHERE YEARWEEK(Date, 1) = YEARWEEK(CURDATE(), 1)";
                break;
            case "month":
                sql = "SELECT SUM(Total_Amount) FROM OrderTable WHERE YEAR(Date) = YEAR(CURDATE()) AND MONTH(Date) = MONTH(CURDATE())";
                break;
            case "year":
                sql = "SELECT SUM(Total_Amount) FROM OrderTable WHERE YEAR(Date) = YEAR(CURDATE())";
                break;
            default:
                sql = "SELECT SUM(Total_Amount) FROM OrderTable";
        }
        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getDouble(1) : 0.0;
    }

    public static double getTotalSales() throws SQLException {
        String sql = "SELECT SUM(Total_Amount) FROM OrderTable";
        ResultSet rs = CrudUtil.execute(sql);
        return rs.next() ? rs.getDouble(1) : 0.0;
    }

    public static boolean cancelOrderAndRestoreInventory(String orderId) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            // 1. Get all inventory items in the order
            String getItemsSql = "SELECT Inv_ID, Qty FROM OrderBookContain WHERE O_ID = ?";
            PreparedStatement getItemsStmt = connection.prepareStatement(getItemsSql);
            getItemsStmt.setString(1, orderId);
            ResultSet rs = getItemsStmt.executeQuery();

            // 2. Restore inventory for each item
            String updateInventorySql = "UPDATE Inventory SET Stock_Qty = Stock_Qty + ? WHERE Inv_ID = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateInventorySql);

            while (rs.next()) {
                int invId = rs.getInt("Inv_ID");
                int qty = rs.getInt("Qty");
                updateStmt.setInt(1, qty);
                updateStmt.setInt(2, invId);
                updateStmt.addBatch();
            }

            updateStmt.executeBatch();

            // 3. Update order status to Cancelled
            String updateOrderSql = "UPDATE OrderTable SET Status = 'Cancelled' WHERE O_ID = ?";
            PreparedStatement orderStmt = connection.prepareStatement(updateOrderSql);
            orderStmt.setString(1, orderId);
            int orderUpdated = orderStmt.executeUpdate();

            if (orderUpdated > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }
}