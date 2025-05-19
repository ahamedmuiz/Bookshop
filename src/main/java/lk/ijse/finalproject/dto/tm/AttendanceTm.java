package lk.ijse.finalproject.dto.tm;

import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceTm {
    private SimpleStringProperty time;
    private SimpleStringProperty type;

    public String getTime() {
        return time.get();
    }

    public String getType() {
        return type.get();
    }
}