package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.AttendanceDto;
import lk.ijse.finalproject.dto.EmployeeDto;
import lk.ijse.finalproject.dto.SalaryDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalaryModel {
    public static boolean saveSalary(SalaryDto dto) throws SQLException {
        String sql = "INSERT INTO Salary(Sal_ID, E_ID, Month, Amount) VALUES(?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getSalId(), dto.getEId(), dto.getMonth(), dto.getAmount());
    }

    public static String generateNextSalaryId() throws SQLException {
        String sql = "SELECT Sal_ID FROM Salary ORDER BY Sal_ID DESC LIMIT 1";
        ResultSet rs = CrudUtil.execute(sql);

        if (rs.next()) {
            String id = rs.getString("Sal_ID");
            int num = Integer.parseInt(id.replace("S", "")) + 1;
            return String.format("S%03d", num);
        }
        return "S001";
    }

    public static List<AttendanceDto> getAttendanceForMonth(int empId, String month, int year) throws SQLException {
        YearMonth yearMonth = YearMonth.of(year, getMonthNumber(month));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Modified to group by date and get first clock-in/last clock-out
        String sql = "SELECT a.date, " +
                "MIN(a.time_in) as first_in, " +
                "MAX(a.time_out) as last_out, " +
                "SUM(a.hours_worked) as daily_hours, " +
                "e.hourly_rate " +
                "FROM Attendance a " +
                "JOIN Employee e ON a.e_id = e.E_ID " +
                "WHERE a.e_id = ? AND a.date BETWEEN ? AND ? " +
                "AND a.time_out IS NOT NULL " +
                "GROUP BY a.date, e.hourly_rate " +
                "ORDER BY a.date";

        ResultSet rs = CrudUtil.execute(sql, empId, startDate, endDate);

        List<AttendanceDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new AttendanceDto(
                    null, // att_id not needed here
                    empId,
                    null, // empName not needed
                    rs.getDate("date").toLocalDate(),
                    rs.getTime("first_in").toLocalTime(),
                    rs.getTime("last_out").toLocalTime(),
                    rs.getDouble("daily_hours"),
                    "Completed", // status
                    rs.getDouble("hourly_rate")
            ));
        }
        return list;
    }

    private static int getMonthNumber(String monthName) {
        return switch (monthName.toLowerCase()) {
            case "january" -> 1;
            case "february" -> 2;
            case "march" -> 3;
            case "april" -> 4;
            case "may" -> 5;
            case "june" -> 6;
            case "july" -> 7;
            case "august" -> 8;
            case "september" -> 9;
            case "october" -> 10;
            case "november" -> 11;
            case "december" -> 12;
            default -> 0;
        };
    }

    public static EmployeeDto getEmployee(int empId) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE E_ID = ?";
        ResultSet rs = CrudUtil.execute(sql, empId);

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

    public static boolean isSalaryPaid(int empId, String month, int year) throws SQLException {
        String sql = "SELECT * FROM Salary WHERE E_ID = ? AND Month = ?";
        ResultSet rs = CrudUtil.execute(sql, empId, month);
        return rs.next();
    }

    public static List<SalaryDto> getAllSalaries() throws SQLException {
        String sql = "SELECT * FROM Salary";
        ResultSet rs = CrudUtil.execute(sql);

        List<SalaryDto> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new SalaryDto(
                    rs.getString("Sal_ID"),
                    rs.getInt("E_ID"),
                    rs.getString("Month"),
                    rs.getInt("year"),
                    rs.getDouble("Amount"),
                    rs.getDate("payment_date").toLocalDate(),
                    rs.getDouble("hourly_rate")
            ));
        }
        return list;
    }
}