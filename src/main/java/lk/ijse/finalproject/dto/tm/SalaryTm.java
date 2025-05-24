package lk.ijse.finalproject.dto.tm;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class SalaryTm {
    private SimpleObjectProperty<LocalDate> date;
    private SimpleStringProperty day;
    private SimpleDoubleProperty hoursWorked;
    private SimpleDoubleProperty dailyWage;
    private SimpleStringProperty status;
    private SimpleDoubleProperty hourlyRate;

    public SalaryTm(LocalDate date, String day, double hoursWorked, double dailyWage, String status, double hourlyRate) {
        this.date = new SimpleObjectProperty<>(date);
        this.day = new SimpleStringProperty(day);
        this.hoursWorked = new SimpleDoubleProperty(hoursWorked);
        this.dailyWage = new SimpleDoubleProperty(dailyWage);
        this.status = new SimpleStringProperty(status);
        this.hourlyRate = new SimpleDoubleProperty(hourlyRate);
    }

    // Getters
    public LocalDate getDate() {
        return date.get();
    }

    public String getDay() {
        return day.get();
    }

    public double getHoursWorked() {
        return hoursWorked.get();
    }

    public double getDailyWage() {
        return dailyWage.get();
    }

    public String getStatus() {
        return status.get();
    }

    public double getHourlyRate() {
        return hourlyRate.get();
    }
}