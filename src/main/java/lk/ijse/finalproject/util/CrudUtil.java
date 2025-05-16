package lk.ijse.finalproject.util;

import lk.ijse.finalproject.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrudUtil {
    public static <T> T execute(String sql, Object... params) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            pstm.setObject(i + 1, params[i]);
        }

        if (sql.trim().toUpperCase().startsWith("SELECT")) {
            return (T) pstm.executeQuery();
        }
        return (T) (Boolean) (pstm.executeUpdate() > 0);
    }

    public static <T> List<T> executeAndGetList(String sql, Class<T> type, Object... params) throws SQLException {
        ResultSet rs = execute(sql, params);
        List<T> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            try {
                T instance = type.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    type.getMethod("set" + capitalize(columnName), value.getClass())
                            .invoke(instance, value);
                }
                list.add(instance);
            } catch (Exception e) {
                throw new SQLException("Failed to map result set to object", e);
            }
        }
        return list;
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}