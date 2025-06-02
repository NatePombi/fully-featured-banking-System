package model;

import util.AutoIdGenerator;
import util.CurrencyFormatter;

import java.math.BigDecimal;
import java.util.*;
/**
 *  a bank account with basic operations like deposit, withdrawal, and transaction tracking.
 */
public class Account implements IAccount{
    private final String ownerUsername;
    private final String accountNumber;
    private final AccountType type;
    private BigDecimal balance;
    private final List<ITransactions> transactions;

    /**
     * Constructor for creating a new account.
     * @param balance Starting balance (must be positive to be accepted).
     * @param type String representing the account type (converted to AccountType enum).
     * @param ownerUsername Username of the account owner.
     */
    public Account(BigDecimal balance, String type,String ownerUsername) {
        this.accountNumber = AutoIdGenerator.generateId(); //auto-generated unique ID
        this.type = Objects.requireNonNull(AccountType.getEnum(type),"Invalid account type");
        this.balance = balance.compareTo(BigDecimal.ZERO)>0?balance: BigDecimal.ZERO;
        transactions = new ArrayList<>();
        this.ownerUsername = ownerUsername;
    }

    public Account(BigDecimal balance, String type,String ownerUsername,String accNum) {
        this.accountNumber = accNum;
        this.type = Objects.requireNonNull(AccountType.getEnum(type),"Invalid account type");
        this.balance = balance.compareTo(BigDecimal.ZERO)>0?balance: BigDecimal.ZERO;
        transactions = new ArrayList<>();
        this.ownerUsername = ownerUsername;
    }
// getters for every field

    @Override
    public String getOwnerUsername() {
        return ownerUsername;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public AccountType getType() {
        return type;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public List<ITransactions> getTransactions() {
        return Collections.unmodifiableList(transactions); //unmodifiable list of transactions (prevents external modification)
    }


    /**
     * Deposits money into the account.
     * @param deposit The amount to deposit (must be positive).
     */
    @Override
    public void deposit(BigDecimal deposit){
        if(deposit.compareTo(BigDecimal.ZERO)>0){
            balance= balance.add(deposit);
            transactions.add(new Transaction("DEPOSIT", deposit, "Deposit to account",accountNumber));
        }else{
            throw new IllegalArgumentException("Amount must be above 0");
        }

    }

    /**
     * Withdraws money from the account.
     * @param withdraw The amount to withdraw (must not exceed balance).
     */
    @Override
    public void withdraw(BigDecimal withdraw){
        if(balance.compareTo(withdraw)>= 0 && withdraw.compareTo(BigDecimal.ZERO) > 0){
            balance = balance.subtract(withdraw);
            transactions.add(new Transaction("WITHDRAW", withdraw, "Withdrawal from account",accountNumber));
        }
        else{
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    /**
     * Returns a formatted string with account information.
     * Uses SessionService to get current user.
     */

    @Override
    public String toString() {
        return String.format("""
                Account Owner: %s
                Account: %s
                Type: %s
                Balance: %s
                """, ownerUsername,accountNumber,type, CurrencyFormatter.getCurrency(balance));

    }
}
