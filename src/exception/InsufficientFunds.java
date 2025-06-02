package exception;

import model.IAccount;

public class InsufficientFunds extends RuntimeException {
    public InsufficientFunds(IAccount acc) {
        super("Insufficient funds. Your balance is R"+ acc.getBalance());
    }
}
