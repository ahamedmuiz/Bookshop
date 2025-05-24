package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceModel {

    public static AttendanceDto getLastAttendance(int empId) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE e_id = ? ORDER BY date DESC, time_in DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql, empId);

        if (rs.next()) {
            return new AttendanceDto(
                    rs.getString("att_id"),
                    rs.getInt("e_id"),
                    null,
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time_in").toLocalTime(),
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status"),
                    0
            );
        }
        return null;
    }

    public static List<AttendanceDto> getAttendanceHistory(int empId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE e_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC, time_in DESC";
        ResultSet rs = CrudUtil.execute(sql, empId, startDate, endDate);

        List<AttendanceDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new AttendanceDto(
                    rs.getString("att_id"),
                    rs.getInt("e_id"),
                    null,
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time_in").toLocalTime(),
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status"),
                    0
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
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("time_in").toLocalTime(),
                    rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null,
                    rs.getDouble("hours_worked"),
                    rs.getString("status"),
                    0
            );
        }
        return null;
    }

    public static boolean hasActiveClockIn(int empId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE e_id = ? AND date = ? AND time_out IS NULL";
        ResultSet rs = CrudUtil.execute(sql, empId, date);
        return rs.next();
    }

    public static double getTotalDailyHours(int empId, LocalDate date) throws SQLException {
        String sql = "SELECT time_in, time_out FROM Attendance " +
                "WHERE e_id = ? AND date = ? AND time_out IS NOT NULL " +
                "ORDER BY time_in";
        ResultSet rs = CrudUtil.execute(sql, empId, date);

        double totalHours = 0.0;
        while (rs.next()) {
            LocalTime timeIn = rs.getTime("time_in").toLocalTime();
            LocalTime timeOut = rs.getTime("time_out").toLocalTime();
            totalHours += calculateHoursWorked(timeIn, timeOut);
        }
        return totalHours;
    }

    private static double calculateHoursWorked(LocalTime timeIn, LocalTime timeOut) {
        long totalMinutes = java.time.Duration.between(timeIn, timeOut).toMinutes();
        return Math.round((totalMinutes / 60.0) * 100) / 100.0;
    }

    public static int getWorkingDaysCount(int empId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT date) as working_days FROM Attendance " +
                "WHERE e_id = ? AND date BETWEEN ? AND ? AND time_out IS NOT NULL";
        ResultSet rs = CrudUtil.execute(sql, empId, startDate, endDate);
        return rs.next() ? rs.getInt("working_days") : 0;
    }

    public static double getTotalHoursWorked(int empId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(hours_worked), 0) as total_hours FROM Attendance " +
                "WHERE e_id = ? AND date BETWEEN ? AND ? AND time_out IS NOT NULL";
        ResultSet rs = CrudUtil.execute(sql, empId, startDate, endDate);
        return rs.next() ? rs.getDouble("total_hours") : 0.0;
    }

    public static boolean saveAttendance(AttendanceDto dto) throws SQLException {
        String sql = "INSERT INTO Attendance(att_id, e_id, date, time_in, status) VALUES(?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getAttId(), dto.getEmpId(), dto.getDate(), dto.getTimeIn(), dto.getStatus());
    }

    public static boolean updateClockOut(AttendanceDto dto) throws SQLException {
        double hoursWorked = calculateHoursWorked(dto.getTimeIn(), dto.getTimeOut());
        dto.setHoursWorked(hoursWorked);

        String sql = "UPDATE Attendance SET time_out = ?, hours_worked = ?, status = ? WHERE att_id = ?";
        return CrudUtil.execute(sql, dto.getTimeOut(), dto.getHoursWorked(), dto.getStatus(), dto.getAttId());
    }
}