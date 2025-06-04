package lk.ijse.finalproject.model;

import lk.ijse.finalproject.dto.SystemUsersDto;
import lk.ijse.finalproject.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemUsersModel {
    public static boolean saveUser(SystemUsersDto dto) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, username, password) VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getUsername(), dto.getPassword());
    }

    public static boolean updateUser(SystemUsersDto dto) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, username = ?, password = ? WHERE user_id = ?";
        return CrudUtil.execute(sql, dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getUsername(), dto.getPassword(), dto.getUserId());
    }

    public static boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return CrudUtil.execute(sql, userId);
    }

    public static SystemUsersDto searchUser(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        ResultSet resultSet = CrudUtil.execute(sql, userId);

        if (resultSet.next()) {
            return new SystemUsersDto(
                    resultSet.getInt("user_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }

    public static SystemUsersDto searchUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        ResultSet resultSet = CrudUtil.execute(sql, email);

        if (resultSet.next()) {
            return new SystemUsersDto(
                    resultSet.getInt("user_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }

    public static SystemUsersDto searchUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        ResultSet resultSet = CrudUtil.execute(sql, username);

        if (resultSet.next()) {
            return new SystemUsersDto(
                    resultSet.getInt("user_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            );
        }
        return null;
    }

    public static List<SystemUsersDto> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        ResultSet resultSet = CrudUtil.execute(sql);
        List<SystemUsersDto> userList = new ArrayList<>();

        while (resultSet.next()) {
            userList.add(new SystemUsersDto(
                    resultSet.getInt("user_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            ));
        }
        return userList;
    }
}