package exception;

public class NoAccountException extends RuntimeException {
    public NoAccountException() {
        super("No Account present pLease create an account first!");
    }
}
