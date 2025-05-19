package lk.ijse.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
    private String attId;
    private int empId;
    private String empName;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
    private double hoursWorked;
    private String status;
}