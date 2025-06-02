package service;

import database.AccountDao;
import exception.InvalidTransactionAmount;
import exception.UnauthorizedAccessException;
import exception.UserNotLoggedInException;
import model.IAccount;
import model.IUser;
import util.CurrencyFormatter;
import util.FindAccount;
import util.OwnershipValidation;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface IBankingService {

     void openAccount(BigDecimal amount, String type);


   void viewBalance(String accountNumber) throws SQLException ;
    }



