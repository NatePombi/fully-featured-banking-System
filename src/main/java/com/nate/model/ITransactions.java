package com.nate.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ITransactions {

     String getToAccount();

     BigDecimal getAmount();

     String getDescription();

     LocalDateTime getTimeStamp();


     String getAccountNumber();

     TransactionType getType();


    /**
     * Returns a formatted string representing the transaction.
     * The output includes type, amount (in currency), and timestamp.
     */
    @Override
    public String toString();
}
