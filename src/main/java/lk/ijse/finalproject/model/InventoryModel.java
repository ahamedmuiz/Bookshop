package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.InventoryDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryModel {
    public int generateNextInventoryId() throws SQLException {
        String sql = "SELECT MAX(Inv_ID) FROM Inventory";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {

            if (resultSet.next()) {
                int maxId = resultSet.getInt(1);
                return maxId + 1;
            }
            return 1;
        }
    }

    public boolean addInventory(InventoryDto dto) throws SQLException {
        String sql = "INSERT INTO Inventory (Inv_ID, Sup_ID, Stock_Qty, Last_Update, Category, Price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getInvId());
            stmt.setInt(2, dto.getSupId());
            stmt.setInt(3, dto.getStockQty());
            stmt.setDate(4, Date.valueOf(dto.getLastUpdate()));
            stmt.setString(5, dto.getCategory());
            stmt.setDouble(6, dto.getPrice());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateInventory(InventoryDto dto) throws SQLException {
        String sql = "UPDATE Inventory SET Sup_ID = ?, Stock_Qty = ?, Last_Update = ?, Category = ?, Price = ? WHERE Inv_ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getSupId());
            stmt.setInt(2, dto.getStockQty());
            stmt.setDate(3, Date.valueOf(dto.getLastUpdate()));
            stmt.setString(4, dto.getCategory());
            stmt.setDouble(5, dto.getPrice());
            stmt.setInt(6, dto.getInvId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteInventory(int id) throws SQLException {
        String sql = "DELETE FROM Inventory WHERE Inv_ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public InventoryDto searchInventory(int id) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID WHERE i.Inv_ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getString("SupplierName"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                );
            }
            return null;
        }
    }

    public List<InventoryDto> getAllInventory() throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<InventoryDto> inventoryList = new ArrayList<>();

            while (rs.next()) {
                inventoryList.add(new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getString("SupplierName"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                ));
            }
            return inventoryList;
        }
    }

    public List<InventoryDto> getInventoryBySupplierId(int supId) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID WHERE i.Sup_ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, supId);
            ResultSet rs = pstm.executeQuery();

            List<InventoryDto> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getString("SupplierName"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                ));
            }
            return list;
        }
    }

    public List<InventoryDto> searchInventoryByAnyField(String searchText) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i " +
                "LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID " +
                "WHERE i.Inv_ID LIKE ? OR i.Category LIKE ? OR s.Name LIKE ? OR i.Sup_ID LIKE ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            // Try to parse as integer for Sup_ID search
            try {
                stmt.setInt(4, Integer.parseInt(searchText));
            } catch (NumberFormatException e) {
                stmt.setInt(4, -1); // Will match nothing if search text isn't a number
            }

            ResultSet rs = stmt.executeQuery();
            List<InventoryDto> results = new ArrayList<>();

            while (rs.next()) {
                results.add(new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getString("SupplierName"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                ));
            }
            return results;
        }
    }

    public boolean updateInventoryWithStockChange(InventoryDto dto, boolean isAddition) throws SQLException {
        String sql = "UPDATE Inventory SET Sup_ID = ?, Stock_Qty = Stock_Qty " +
                (isAddition ? "+" : "-") + " ?, Last_Update = ?, Category = ?, Price = ? " +
                "WHERE Inv_ID = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getSupId());
            stmt.setInt(2, dto.getStockQty());
            stmt.setDate(3, Date.valueOf(dto.getLastUpdate()));
            stmt.setString(4, dto.getCategory());
            stmt.setDouble(5, dto.getPrice());
            stmt.setInt(6, dto.getInvId());

            return stmt.executeUpdate() > 0;
        }
    }
}