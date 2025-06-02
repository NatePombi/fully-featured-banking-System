package database;

import exception.UnauthorizedAccessException;
import model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface IAccountDao {

    // Add a new account

     void addAccount(IAccount account) throws SQLException;

     List<IAccount> getAll() throws SQLException;

    // Update account balance
     void updateAccountBalanceTransfer(String fromAccountNumber,String toAccountNumber, BigDecimal newFrom, BigDecimal newTo, BigDecimal amount) throws SQLException;


    // Update account balance
     void updateAccountBalanceDeposit(String accountNumber, BigDecimal amount, IUser user) throws SQLException;


     void updateAccountBalanceWithdraw(String accountNumber, BigDecimal amount, IUser user) throws SQLException;


     List<IAccount> getAllAccountsByUsername(String username) throws SQLException;

    IAccount getAccountsByAccountNumber(String username) throws SQLException;


    void deleteAccountByAccountNumber(String accountNumber) throws SQLException;

}
