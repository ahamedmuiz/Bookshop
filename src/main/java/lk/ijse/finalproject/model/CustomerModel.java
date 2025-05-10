package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.CustomerDto;
import lk.ijse.finalproject.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerModel {

    public boolean addCustomer(CustomerDto dto) throws SQLException {
        String sql = "INSERT INTO Customer (C_ID, Name, Email, Contact) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, dto.getCId());
            ps.setString(2, dto.getName());
            ps.setString(3, dto.getEmail());
            ps.setString(4, dto.getContact());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCustomer(CustomerDto dto) throws SQLException {
        String sql = "UPDATE Customer SET Name=?, Email=?, Contact=? WHERE C_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dto.getName());
            ps.setString(2, dto.getEmail());
            ps.setString(3, dto.getContact());
            ps.setInt(4, dto.getCId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM Customer WHERE C_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public CustomerDto searchCustomer(int id) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE C_ID=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CustomerDto(
                        rs.getInt("C_ID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Contact")
                );
            }
            return null;
        }
    }

    public List<CustomerDto> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM Customer";
        try (Connection con = DBConnection.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            List<CustomerDto> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new CustomerDto(
                        rs.getInt("C_ID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Contact")
                ));
            }
            return list;
        }
    }
}
