package com.nate.service;

import com.nate.model.IUser;

import java.sql.SQLException;

public interface IUserService {
    boolean registerUser(String username, String password, String name) throws SQLException;


    boolean loginUser(String username, String password) throws SQLException;

    IUser getUserByUsername(String username) throws SQLException;
}
