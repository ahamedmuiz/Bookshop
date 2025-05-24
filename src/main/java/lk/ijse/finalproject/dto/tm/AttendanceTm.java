package lk.ijse.finalproject.dto.tm;

import javafx.beans.property.SimpleStringProperty;

public class AttendanceTm {
    private SimpleStringProperty time;
    private SimpleStringProperty type;

    public AttendanceTm(SimpleStringProperty time, SimpleStringProperty type) {
        this.time = time;
        this.type = type;
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }
}