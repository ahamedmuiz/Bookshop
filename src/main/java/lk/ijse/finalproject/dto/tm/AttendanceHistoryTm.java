package lk.ijse.finalproject.dto.tm;

import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceHistoryTm {
    private SimpleStringProperty date;
    private SimpleStringProperty firstIn;
    private SimpleStringProperty lastOut;
    private SimpleStringProperty totalHours;

    public String getDate() {
        return date.get();
    }

    public String getFirstIn() {
        return firstIn.get();
    }

    public String getLastOut() {
        return lastOut.get();
    }

    public String getTotalHours() {
        return totalHours.get();
    }
}