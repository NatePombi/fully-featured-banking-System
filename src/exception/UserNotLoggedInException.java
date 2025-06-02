package exception;

public class UserNotLoggedInException extends RuntimeException {
    //Exception for when a user is not logged in
    public UserNotLoggedInException() {
        super("You must be logged in to perform this action.");
    }
}
