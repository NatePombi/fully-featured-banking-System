package exception;

public class AccountNotFoundException extends RuntimeException {

    //Exception for when account not found
    public AccountNotFoundException(String accountNumber){
        super("Account with number " + accountNumber + " not found!");
    }
}
