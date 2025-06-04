package lk.ijse.finalproject.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDto {
    private String attId;
    private int empId;
    private String empName;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private double hoursWorked;
    private String status;
    private double hourlyRate;

    public AttendanceDto(String attId, int empId, String empName, LocalDate date, LocalTime timeIn,
                         LocalTime timeOut, double hoursWorked, String status, double hourlyRate) {
        this.attId = attId;
        this.empId = empId;
        this.empName = empName;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.hoursWorked = hoursWorked;
        this.status = status;
        this.hourlyRate = hourlyRate;
    }

    public AttendanceDto(String attId, int empId, LocalDate date, LocalTime timeIn, String status) {
        this(attId, empId, null, date, timeIn, null, 0, status, 0);
    }

    public String getAttId() {
        return attId;
    }

    public void setAttId(String attId) {
        this.attId = attId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(LocalTime timeIn) {
        this.timeIn = timeIn;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalTime timeOut) {
        this.timeOut = timeOut;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}