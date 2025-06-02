package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.CustomerDto;
import lk.ijse.finalproject.db.DBConnection;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerModel {

    public int generateNextCustomerId() throws SQLException {
        String sql = "SELECT C_ID FROM Customer ORDER BY C_ID DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            return 1;
        }
    }

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

    public String findNameById(String customerId) throws SQLException {
        String sql = "SELECT Name FROM Customer WHERE C_ID = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("Name") : null;
        }
    }

    public ArrayList<String> getAllCustomerIds() throws SQLException {
        String sql = "SELECT C_ID FROM Customer";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ArrayList<String> idList = new ArrayList<>();
            while (rs.next()) {
                idList.add(rs.getString("C_ID"));
            }
            return idList;
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
            return rs.next() ? new CustomerDto(
                    rs.getInt("C_ID"),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Contact")) : null;
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
                        rs.getString("Contact")));
            }
            return list;
        }
    }

    public List<CustomerDto> searchCustomersByIdOrName(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE C_ID LIKE ? OR Name LIKE ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            ps.setString(1, likeTerm);
            ps.setString(2, likeTerm);

            ResultSet rs = ps.executeQuery();
            List<CustomerDto> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new CustomerDto(
                        rs.getInt("C_ID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Contact")));
            }
            return list;
        }
    }
}