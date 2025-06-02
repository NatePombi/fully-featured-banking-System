package model;

import java.math.BigDecimal;
import java.util.List;

public interface IAccount {

    String getOwnerUsername();
    String getAccountNumber();
    AccountType getType();
    BigDecimal getBalance();
    List<ITransactions> getTransactions();
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    String toString();

}
