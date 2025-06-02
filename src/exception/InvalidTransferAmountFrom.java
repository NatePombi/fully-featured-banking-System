package exception;

import service.SessionService;

import java.math.BigDecimal;

public class InvalidTransferAmountFrom extends RuntimeException {
    //Exception for when a transfer is being made from an account with fewer funds than the transfer amount
    public InvalidTransferAmountFrom(BigDecimal amount) {
        super("Invalid transaction amount: R" + amount +"." + " Your balance must be greater than R" + amount + ". Current balance is less than R" + amount );
    }
}
