package lk.ijse.finalproject.dto.tm;

import javafx.beans.property.SimpleStringProperty;

public class AttendanceHistoryTm {
    private SimpleStringProperty date;
    private SimpleStringProperty firstIn;
    private SimpleStringProperty lastOut;
    private SimpleStringProperty totalHours;

    public AttendanceHistoryTm(SimpleStringProperty date, SimpleStringProperty firstIn,
                               SimpleStringProperty lastOut, SimpleStringProperty totalHours) {
        this.date = date;
        this.firstIn = firstIn;
        this.lastOut = lastOut;
        this.totalHours = totalHours;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getFirstIn() {
        return firstIn.get();
    }

    public SimpleStringProperty firstInProperty() {
        return firstIn;
    }

    public void setFirstIn(String firstIn) {
        this.firstIn.set(firstIn);
    }

    public String getLastOut() {
        return lastOut.get();
    }

    public SimpleStringProperty lastOutProperty() {
        return lastOut;
    }

    public void setLastOut(String lastOut) {
        this.lastOut.set(lastOut);
    }

    public String getTotalHours() {
        return totalHours.get();
    }

    public SimpleStringProperty totalHoursProperty() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours.set(totalHours);
    }
}