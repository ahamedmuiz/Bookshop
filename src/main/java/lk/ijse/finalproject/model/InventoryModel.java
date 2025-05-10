package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.InventoryDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryModel {


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
        String sql = "SELECT * FROM Inventory WHERE Inv_ID = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
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
        String sql = "SELECT * FROM Inventory";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery(sql);

            List<InventoryDto> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                ));
            }
            return list;
        }
    }

    public List<InventoryDto> getInventoryBySupplierId(int supId) throws SQLException {
        String sql = "SELECT * FROM Inventory WHERE Sup_ID = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supId);
            ResultSet rs = stmt.executeQuery();

            List<InventoryDto> inventoryList = new ArrayList<>();
            while (rs.next()) {
                InventoryDto dto = new InventoryDto(
                        rs.getInt("Inv_ID"),
                        rs.getInt("Sup_ID"),
                        rs.getInt("Stock_Qty"),
                        rs.getDate("Last_Update").toLocalDate(),
                        rs.getString("Category"),
                        rs.getDouble("Price")
                );
                inventoryList.add(dto);
            }
            return inventoryList;
        }

    }
}
