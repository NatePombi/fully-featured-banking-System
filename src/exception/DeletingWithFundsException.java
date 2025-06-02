package exception;

import java.math.BigDecimal;

public class DeletingWithFundsException extends RuntimeException {
    //Exception for when an account is trying to get deleted with funds
    public DeletingWithFundsException(BigDecimal amount, String accountNumber) {
        super("Failed!!: Account with number "+ accountNumber + " has a balance: R" + amount + " unable to be deleted due to funds present");
    }
}
