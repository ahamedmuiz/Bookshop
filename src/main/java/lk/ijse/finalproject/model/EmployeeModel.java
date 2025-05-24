package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeModel {


    public static boolean addEmployee(EmployeeDto employee) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Employee (E_ID, E_Name, E_Email, E_Contact, hourly_rate) VALUES (?, ?, ?, ?, ?)";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = con.prepareStatement(sql);
        pstm.setInt(1, employee.getE_ID());
        pstm.setString(2, employee.getE_Name());
        pstm.setString(3, employee.getE_Email());
        pstm.setString(4, employee.getE_Contact());
        pstm.setDouble(5, employee.getHourly_rate());
        return pstm.executeUpdate() > 0;
    }

    public static boolean deleteEmployee(String eId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Employee WHERE E_ID = ?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = con.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(eId));
        return pstm.executeUpdate() > 0;
    }

    public static boolean updateEmployee(EmployeeDto employee) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Employee SET E_Name = ?, E_Email = ?, E_Contact = ?, hourly_rate = ? WHERE E_ID = ?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = con.prepareStatement(sql);
        pstm.setString(1, employee.getE_Name());
        pstm.setString(2, employee.getE_Email());
        pstm.setString(3, employee.getE_Contact());
        pstm.setDouble(4, employee.getHourly_rate());
        pstm.setInt(5, employee.getE_ID());
        return pstm.executeUpdate() > 0;
    }

    public static EmployeeDto searchEmployee(String eId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Employee WHERE E_ID = ?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = con.prepareStatement(sql);
        pstm.setInt(1, Integer.parseInt(eId));
        ResultSet rs = pstm.executeQuery();

        if (rs.next()) {
            return new EmployeeDto(
                    rs.getInt("E_ID"),
                    rs.getString("E_Name"),
                    rs.getString("E_Email"),
                    rs.getString("E_Contact"),
                    rs.getDouble("hourly_rate")
            );
        }
        return null;
    }

    public static List<EmployeeDto> getAllEmployees() throws SQLException, ClassNotFoundException {
        List<EmployeeDto> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM Employee";
        Connection con = DBConnection.getInstance().getConnection();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(sql);

        while (rs.next()) {
            EmployeeDto employee = new EmployeeDto(
                    rs.getInt("E_ID"),
                    rs.getString("E_Name"),
                    rs.getString("E_Email"),
                    rs.getString("E_Contact"),
                    rs.getDouble("hourly_rate")
            );
            employeeList.add(employee);
        }
        return employeeList;
    }
}
