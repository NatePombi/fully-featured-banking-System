package com.nate.service;

import com.nate.model.IUser;

public class SessionService implements ISessionService{

    private IUser currentUser;

    // Sets the current user (used after successful login)
    @Override
    public void setCurrentUser(IUser currentUser){
        this.currentUser =currentUser;
    }

    // Returns the currently logged-in user, or throws if no one is logged in
    @Override
    public IUser getCurrentUser(){
        if(currentUser == null){
            throw new IllegalStateException("No currentUser is signed in");
        }
        return currentUser;
    }

    // Logs out the current user
    @Override
    public void logOut(){
        currentUser = null;
    }

    // Checks if a user is currently logged in
    @Override
    public boolean isLoggedIn(){
        return this.currentUser != null;
    }


}