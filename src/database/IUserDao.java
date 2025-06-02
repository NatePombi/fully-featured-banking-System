package database;

import model.IUser;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IUserDao {

    // Add a new user
     void addUser(IUser user) throws SQLException;

    // Find user by username
     IUser findUserByUsername(String username) throws SQLException;

    // Update user's name and password (example)
     void updateUser(IUser user) throws SQLException;

    // Delete user by username
     void deleteUser(String username) throws SQLException;
}
