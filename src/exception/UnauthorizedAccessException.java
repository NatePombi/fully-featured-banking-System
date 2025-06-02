package exception;

public class UnauthorizedAccessException extends RuntimeException {
    //Exception for when a user is trying to access an account that does not belong to them
    public UnauthorizedAccessException(String accountNumber) {
        super("Access denied: You do not own an account with this number '" + accountNumber+ "'.");
    }
}
