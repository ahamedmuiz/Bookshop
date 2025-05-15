package lk.ijse.finalproject.model;

import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.dto.SupplierDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {

    public static boolean addSupplier(SupplierDto dto) throws SQLException {
        Connection con = DBConnection.getInstance().getConnection();

        String checkSql = "SELECT Sup_ID FROM Supplier WHERE Sup_ID = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkSql);
        checkStmt.setInt(1, dto.getSupId());
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            return false;
        }

        String sql = "INSERT INTO Supplier(Sup_ID, Name, Contact, Address) VALUES(?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, dto.getSupId());
        stmt.setString(2, dto.getName());
        stmt.setString(3, dto.getContact());
        stmt.setString(4, dto.getAddress());

        return stmt.executeUpdate() > 0;
    }


    public boolean updateSupplier(SupplierDto dto) throws SQLException {
        String sql = "UPDATE Supplier SET Name=?, Contact=?, Address=? WHERE Sup_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getContact());
            ps.setString(3, dto.getAddress());
            ps.setInt(4, dto.getSupId());
            return ps.executeUpdate() > 0;
        }
    }

    // Delete a supplier by ID
    public boolean deleteSupplier(int supId) throws SQLException {
        String sql = "DELETE FROM Supplier WHERE Sup_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supId);
            return ps.executeUpdate() > 0;
        }
    }

    public SupplierDto searchSupplier(int supId) throws SQLException {
        String sql = "SELECT * FROM Supplier WHERE Sup_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SupplierDto(
                        rs.getInt("Sup_ID"),
                        rs.getString("Name"),
                        rs.getString("Contact"),
                        rs.getString("Address")
                );
            }
        }
        return null;
    }

    public List<SupplierDto> getAllSuppliers() throws SQLException {
        List<SupplierDto> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier";
        try (Connection con = DBConnection.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SupplierDto(
                        rs.getInt("Sup_ID"),
                        rs.getString("Name"),
                        rs.getString("Contact"),
                        rs.getString("Address")
                ));
            }
        }
        return list;
    }

    public List<Integer> getAllSupplierIds() throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT Sup_ID FROM Supplier";
        try (Connection con = DBConnection.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getInt("Sup_ID"));
            }
        }
        return list;
    }

    public boolean isSupplierExists(int supplierId) throws SQLException {
        String sql = "SELECT Sup_ID FROM Supplier WHERE Sup_ID = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, supplierId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        }
    }
}