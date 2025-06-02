package exception;

import java.math.BigDecimal;

public class InvalidTransactionAmount extends RuntimeException {
    //Exception for when a transaction is being made with an amount less than 0
    public InvalidTransactionAmount(BigDecimal amount) {
        super("Invalid transaction amount: R" + amount +"." + " Amount must be greater than 0." );
    }
}
