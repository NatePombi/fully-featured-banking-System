package service;

import model.IUser;

public interface ISessionService {

     void setCurrentUser(IUser currentUser);

     IUser getCurrentUser();

     void logOut();

    boolean isLoggedIn();
}
