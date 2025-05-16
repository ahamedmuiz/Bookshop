package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.SupplierDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {

    public static boolean addSupplier(SupplierDto dto) throws SQLException {
        if (isSupplierExists(dto.getSupId())) {
            return false; // Supplier already exists
        }

        String sql = "INSERT INTO Supplier(Sup_ID, Name, Contact, Address) VALUES(?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getSupId(), dto.getName(), dto.getContact(), dto.getAddress());
    }

    public static boolean updateSupplier(SupplierDto dto) throws SQLException {
        String sql = "UPDATE Supplier SET Name=?, Contact=?, Address=? WHERE Sup_ID=?";
        return CrudUtil.execute(sql, dto.getName(), dto.getContact(), dto.getAddress(), dto.getSupId());
    }

    public static boolean deleteSupplier(int supId) throws SQLException {
        String sql = "DELETE FROM Supplier WHERE Sup_ID=?";
        return CrudUtil.execute(sql, supId);
    }

    public static SupplierDto searchSupplier(int supId) throws SQLException {
        String sql = "SELECT * FROM Supplier WHERE Sup_ID=?";
        ResultSet rs = CrudUtil.execute(sql, supId);

        if (rs.next()) {
            return new SupplierDto(
                    rs.getInt("Sup_ID"),
                    rs.getString("Name"),
                    rs.getString("Contact"),
                    rs.getString("Address")
            );
        }
        return null;
    }

    public static List<SupplierDto> getAllSuppliers() throws SQLException {
        String sql = "SELECT * FROM Supplier";
        ResultSet rs = CrudUtil.execute(sql);

        List<SupplierDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SupplierDto(
                    rs.getInt("Sup_ID"),
                    rs.getString("Name"),
                    rs.getString("Contact"),
                    rs.getString("Address")
            ));
        }
        return list;
    }

    // Updated to return List<Integer> as required by SupplierController
    public static List<Integer> getAllSupplierIds() throws SQLException {
        String sql = "SELECT Sup_ID FROM Supplier";
        ResultSet rs = CrudUtil.execute(sql);

        List<Integer> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rs.getInt("Sup_ID"));
        }
        return list;
    }

    public static boolean isSupplierExists(int supplierId) throws SQLException {
        String sql = "SELECT Sup_ID FROM Supplier WHERE Sup_ID = ?";
        ResultSet rs = CrudUtil.execute(sql, supplierId);
        return rs.next();
    }

    // New method to get supplier names as List<String>
    public static List<String> getAllSupplierNames() throws SQLException {
        String sql = "SELECT Name FROM Supplier";
        ResultSet rs = CrudUtil.execute(sql);

        List<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rs.getString("Name"));
        }
        return list;
    }

    // Helper method to get supplier name by ID (returns String)
    public static String getSupplierName(Object supplierId) throws SQLException {
        String sql = "SELECT Name FROM Supplier WHERE Sup_ID = ?";
        int id = (supplierId instanceof String) ? Integer.parseInt((String) supplierId) : (Integer) supplierId;
        ResultSet rs = CrudUtil.execute(sql, id);
        return rs.next() ? rs.getString("Name") : null;
    }


    public static int generateNextSupplierId() throws SQLException {
        String sql = "SELECT Sup_ID FROM Supplier ORDER BY Sup_ID DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql);

        if (rs.next()) {
            return rs.getInt(1) + 1;
        }
        return 1; // Starting ID if no suppliers exist
    }
}