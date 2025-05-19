package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceModel {
    public static boolean saveAttendance(AttendanceDto dto) throws SQLException {
        String sql = "INSERT INTO Attendance(att_id, e_id, date, time_in, status) VALUES(?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getAttId(), dto.getEmpId(), dto.getDate(), dto.getTimeIn(), dto.getStatus());
    }

    public static boolean updateClockOut(AttendanceDto dto) throws SQLException {
        String sql = "UPDATE Attendance SET time_out = ?, hours_worked = ?, status = ? WHERE att_id = ?";
        return CrudUtil.execute(sql, dto.getTimeOut(), dto.getHoursWorked(), dto.getStatus(), dto.getAttId());
    }

    public static AttendanceDto getLastAttendance(int empId) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE e_id = ? ORDER BY date DESC, time_in DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql, empId);

        if (rs.next()) {
            return new AttendanceDto(
                    rs.getString("att_id"),
                    rs.getInt("e_id"),
                    null,
                    rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                    rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null,
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status")
            );
        }
        return null;
    }

    public static List<AttendanceDto> getAttendanceHistory(int empId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT a.*, e.E_Name FROM Attendance a JOIN Employee e ON a.e_id = e.E_ID " +
                "WHERE a.e_id = ? AND a.date BETWEEN ? AND ? ORDER BY a.date DESC";
        ResultSet rs = CrudUtil.execute(sql, empId, startDate, endDate);

        List<AttendanceDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new AttendanceDto(
                    rs.getString("att_id"),
                    rs.getInt("e_id"),
                    rs.getString("E_Name"),
                    rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                    rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null,
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status")
            ));
        }
        return list;
    }

    public static List<EmployeeDto> getAllEmployees() throws SQLException {
        String sql = "SELECT * FROM Employee";
        ResultSet rs = CrudUtil.execute(sql);

        List<EmployeeDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new EmployeeDto(
                    rs.getInt("E_ID"),
                    rs.getString("E_Name"),
                    rs.getString("E_Email"),
                    rs.getString("E_Contact")
            ));
        }
        return list;
    }

    public static String generateNextAttendanceId() throws SQLException {
        String sql = "SELECT att_id FROM Attendance ORDER BY att_id DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql);

        if (rs.next()) {
            String id = rs.getString("att_id");
            int num = Integer.parseInt(id.replace("ATT", "")) + 1;
            return String.format("ATT%03d", num);
        }
        return "ATT001";
    }

    public static AttendanceDto getAttendanceById(String attId) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE att_id = ?";
        ResultSet rs = CrudUtil.execute(sql, attId);

        if (rs.next()) {
            return new AttendanceDto(
                    rs.getString("att_id"),
                    rs.getInt("e_id"),
                    null,
                    rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                    rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null,
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status")
            );
        }
        return null;
    }
}