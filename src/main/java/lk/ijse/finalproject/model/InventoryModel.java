package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.InventoryDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryModel {
    public static boolean saveInventory(InventoryDto dto) throws SQLException {
        String sql = "INSERT INTO Inventory(Inv_ID, Sup_ID, Stock_Qty, Last_Update, Category, Price) VALUES(?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql,
                dto.getInvId(),
                dto.getSupId(),
                dto.getStockQty(),
                dto.getLastUpdate(),
                dto.getCategory(),
                dto.getPrice()
        );
    }

    public static boolean updateInventory(InventoryDto dto) throws SQLException {
        String sql = "UPDATE Inventory SET Sup_ID = ?, Stock_Qty = ?, Last_Update = ?, Category = ?, Price = ? WHERE Inv_ID = ?";
        return CrudUtil.execute(sql,
                dto.getSupId(),
                dto.getStockQty(),
                dto.getLastUpdate(),
                dto.getCategory(),
                dto.getPrice(),
                dto.getInvId()
        );
    }

    public static boolean deleteInventory(String invId) throws SQLException {
        String sql = "DELETE FROM Inventory WHERE Inv_ID = ?";
        return CrudUtil.execute(sql, invId);
    }

    public static List<InventoryDto> getAllInventory() throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID";
        ResultSet resultSet = CrudUtil.execute(sql);

        List<InventoryDto> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new InventoryDto(
                    resultSet.getInt("Inv_ID"),
                    resultSet.getInt("Sup_ID"),
                    resultSet.getString("SupplierName"),
                    resultSet.getInt("Stock_Qty"),
                    resultSet.getDate("Last_Update").toLocalDate(),
                    resultSet.getString("Category"),
                    resultSet.getDouble("Price")
            ));
        }
        return list;
    }

    public static List<InventoryDto> searchInventoryByAnyField(String searchText) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID " +
                "WHERE i.Category LIKE ?";
        searchText = "%" + searchText + "%";
        ResultSet resultSet = CrudUtil.execute(sql, searchText);

        List<InventoryDto> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new InventoryDto(
                    resultSet.getInt("Inv_ID"),
                    resultSet.getInt("Sup_ID"),
                    resultSet.getString("SupplierName"),
                    resultSet.getInt("Stock_Qty"),
                    resultSet.getDate("Last_Update").toLocalDate(),
                    resultSet.getString("Category"),
                    resultSet.getDouble("Price")
            ));
        }
        return list;
    }

    public static List<InventoryDto> searchInventoryByIdOrName(String searchText) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID " +
                "WHERE i.Inv_ID = ?";
        ResultSet resultSet = CrudUtil.execute(sql, searchText);

        List<InventoryDto> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new InventoryDto(
                    resultSet.getInt("Inv_ID"),
                    resultSet.getInt("Sup_ID"),
                    resultSet.getString("SupplierName"),
                    resultSet.getInt("Stock_Qty"),
                    resultSet.getDate("Last_Update").toLocalDate(),
                    resultSet.getString("Category"),
                    resultSet.getDouble("Price")
            ));
        }
        return list;
    }

    public static String generateNextInventoryId() throws SQLException {
        String sql = "SELECT Inv_ID FROM Inventory ORDER BY Inv_ID DESC LIMIT 1";
        ResultSet resultSet = CrudUtil.execute(sql);

        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int lastIdNum = Integer.parseInt(lastId);
            return String.format("%03d", lastIdNum + 1);
        }
        return "001";
    }

    public static boolean updateInventoryQuantity(String invId, int newQty) throws SQLException {
        String sql = "UPDATE Inventory SET Stock_Qty = ?, Last_Update = CURRENT_DATE WHERE Inv_ID = ?";
        return CrudUtil.execute(sql, newQty, invId);
    }

    public static List<InventoryDto> getInventoryBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT i.*, s.Name AS SupplierName FROM Inventory i " +
                "LEFT JOIN Supplier s ON i.Sup_ID = s.Sup_ID " +
                "WHERE i.Sup_ID = ?";
        ResultSet resultSet = CrudUtil.execute(sql, supplierId);

        List<InventoryDto> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new InventoryDto(
                    resultSet.getInt("Inv_ID"),
                    resultSet.getInt("Sup_ID"),
                    resultSet.getString("SupplierName"),
                    resultSet.getInt("Stock_Qty"),
                    resultSet.getDate("Last_Update").toLocalDate(),
                    resultSet.getString("Category"),
                    resultSet.getDouble("Price")
            ));
        }
        return list;
    }
}