module lk.ijse.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens lk.ijse.finalproject.controller to javafx.fxml;
    opens lk.ijse.finalproject.db to javafx.fxml;
    opens lk.ijse.finalproject.dto to javafx.base; // Add this line to open the package


    exports lk.ijse.finalproject;
    exports lk.ijse.finalproject.controller;
    exports lk.ijse.finalproject.db;
}

