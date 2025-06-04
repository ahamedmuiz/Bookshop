package lk.ijse.finalproject.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Bookshop",USER = "Ahamed",PASSWORD = "*Aa61018570";

    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() {
        connect();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database Connection Failed!");
        }
    }

    public static DBConnection getInstance() {
        if (dbConnection == null || dbConnection.isConnectionClosed()) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        if (isConnectionClosed()) {
            connect();
        }
        return connection;
    }

    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}
