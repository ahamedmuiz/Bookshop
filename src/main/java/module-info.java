module lk.ijse.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jasperreports;

    opens lk.ijse.finalproject.controller to javafx.fxml;
    opens lk.ijse.finalproject.db to javafx.fxml;
    opens lk.ijse.finalproject.dto to javafx.base;
    opens lk.ijse.finalproject.dto.tm to javafx.base;

    exports lk.ijse.finalproject;
    exports lk.ijse.finalproject.controller;
    exports lk.ijse.finalproject.db;
    exports lk.ijse.finalproject.dto.tm;

}

