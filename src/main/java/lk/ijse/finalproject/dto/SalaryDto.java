package lk.ijse.finalproject.dto;

import java.time.LocalDate;

public class SalaryDto {
    private String salId;
    private int eId;
    private String month;
    private int year;
    private double amount;
    private LocalDate paymentDate;
    private double hourlyRate;

    public SalaryDto() {
    }

    public SalaryDto(String salId, int eId, String month, int year, double amount, LocalDate paymentDate, double hourlyRate) {
        this.salId = salId;
        this.eId = eId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.hourlyRate = hourlyRate;
    }

    // Getters and Setters
    public String getSalId() {
        return salId;
    }

    public void setSalId(String salId) {
        this.salId = salId;
    }

    public int getEId() {
        return eId;
    }

    public void setEId(int eId) {
        this.eId = eId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}