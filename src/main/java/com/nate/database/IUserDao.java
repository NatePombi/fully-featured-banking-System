package com.nate.database;

import com.nate.model.IUser;

import java.sql.SQLException;

public interface IUserDao {

    // Add a new user
     void addUser(IUser user) throws SQLException;

    // Find user by username
     IUser findUserByUsername(String username) throws SQLException;



    // Delete user by username
     void deleteUser(String username) throws SQLException;
}
