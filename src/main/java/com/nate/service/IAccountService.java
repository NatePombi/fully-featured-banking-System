package com.nate.service;

import com.nate.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IAccountService {


    IAccount createAccount(BigDecimal balance, String type, IUser user);

    void deposit(String accountNumber, BigDecimal amount) throws SQLException;

    void withdraw(String accountNumber, BigDecimal amount) throws SQLException;

    IAccount getAccountDetails(String accountNumber) throws SQLException;

    List<ITransactions> getTransactions(String accountNumber) throws SQLException;

    boolean deleteAccount(String accountNumber) throws SQLException;

    boolean transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws SQLException;


    List<IAccount> getAllUserAccounts() throws SQLException;

}
