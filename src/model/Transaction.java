package model;


import util.CurrencyFormatter;
import util.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Objects;

/**
 * A transaction record for an account.
 * Includes the type of transaction, amount, description, and timestamp.
 */
public class Transaction implements  ITransactions{
    protected final TransactionType type;
    protected final BigDecimal amount;
    protected final String description;
    protected final LocalDateTime timeStamp;
    protected final String accountNumber;

    /**
     * Constructor to create a new Transaction.
     * @param type The type of the transaction as a string (converted to enum).
     * @param amount The amount of the transaction (must not be null).
     * @param description A short message about the transaction (defaults if null).
     */
    public Transaction(String type, BigDecimal amount, String description,String accountNumber) {
        this.type = Objects.requireNonNull(TransactionType.getEnum(type));
        this.amount = Objects.requireNonNull(amount);
        this.description = description!=null? description : "No details provided";
        timeStamp = LocalDateTime.now(); // Set the timestamp at creation time
        this.accountNumber = accountNumber;
    }

    public Transaction(String type, BigDecimal amount, String description, String accountNumber, LocalDateTime timeStamp) {
        this.type = TransactionType.getEnum(type);
        this.amount = Objects.requireNonNull(amount);
        this.description = description!=null? description : "No details provided";
        this.timeStamp = timeStamp;
        this.accountNumber = accountNumber;
    }


    //Getters for every field

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public TransactionType getType() {
        return type;
    }

    @Override
    public String getToAccount() {
        return "";
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns a formatted string representing the transaction.
     * The output includes type, amount (in currency), and timestamp.
     */
    @Override
    public String toString() {

        return String.format("""
                %-15s |%14s        |  %s
               """,type, CurrencyFormatter.getCurrency(amount), DateTimeFormat.dateFormatter(timeStamp));
    }
}
