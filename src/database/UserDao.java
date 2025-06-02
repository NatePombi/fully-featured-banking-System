package database;

import model.IUser;
import model.User;

import database.*;
import service.SessionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao implements IUserDao{

    // Add a new user
    @Override
    public void addUser(IUser user) throws SQLException {
        String sql = "INSERT INTO users (username, password, salt, name) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getName());
            pstmt.executeUpdate();
        }
    }

    // Find user by username
    @Override
    public IUser findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dbFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                String salt = rs.getString("salt");
                String name = rs.getString("name");
                return new User(username, password, name,salt);
            }
        }
        return null; // user not found
    }

    // Update user's name and password
    @Override
    public void updateUser(IUser user) throws SQLException {
        String sql = "UPDATE users SET password = ?, salt = ?, name = ? WHERE username = ?";
        try (Connection conn = dbFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getSalt());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getUsername());
            pstmt.executeUpdate();
        }
    }

    // Delete user by username
    @Override
    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = dbFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
}
