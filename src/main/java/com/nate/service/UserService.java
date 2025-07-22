package com.nate.service;

import com.nate.database.IUserDao;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.database.Configuration;
import com.nate.util.PasswordUtil;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public class UserService implements IUserService{


    private final IUserDao userDao;
    private final ISessionService service;
    private final PasswordUtil passwordUtil = new PasswordUtil();


    public UserService(ISessionService service, IUserDao userDao){
        this.service = service;
        this.userDao = userDao;
    }
    // Registers a new user
    @Override
    public boolean registerUser(String username, String password, String name) throws SQLException {

        // Validate input fields
        if (username == null || username.isBlank()) {
            throw new NullPointerException("Username is empty");
        }
        if (password == null || password.isBlank()) {
            throw new NullPointerException("Password field is empty");
        }
        if (name == null || name.isBlank()) {
            throw new NullPointerException("Name field is empty");
        }

        // Check if username is already taken
        if (userDao.findUserByUsername(username.trim()) != null) {
            throw new IllegalArgumentException("User already exists!");
        }

        // Create and store the new user
        String salt = passwordUtil.generateSalt();
        String hashPass = passwordUtil.hash(password,salt);
        IUser user = new User(username.trim(), hashPass, name.trim(),salt);
        userDao.addUser(user);

        System.out.println("User successfully registered!");
        return true;
    }

    // Authenticates and logs in a user
    @Override
    public boolean loginUser(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new NullPointerException("Username field is empty!");
        }
        if (password == null || password.isBlank()) {
            throw new NullPointerException("Password field is empty!");
        }

        // Look up user by username
        IUser user = userDao.findUserByUsername(username.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        // Use secure hash comparison to verify password without revealing specific failure reason
        // This helps prevent brute force attacks by not disclosing whether the username or password is incorrect
        boolean isValid = passwordUtil.verifyPassword(password, user.getPassword(), user.getSalt());
        if (!isValid) {
            throw new IllegalArgumentException("Incorrect username or password.");
        }

        // Set the user as logged in
        service.setCurrentUser(user);
        System.out.println("User logged in successfully!");
        return true;
    }

    // Retrieves a user by username
    @Override
    public IUser getUserByUsername(String username) throws SQLException {
        IUser user = userDao.findUserByUsername(username.trim());
        if (user == null) {
            throw new NoSuchElementException("User not found.");
        }
        return user;
    }
}
