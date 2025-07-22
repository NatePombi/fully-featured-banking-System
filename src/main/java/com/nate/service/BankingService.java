package com.nate.service;

import com.nate.database.AccountDao;
import com.nate.database.IAccountDao;
import com.nate.database.ITransactionDao;
import com.nate.database.UserRepo;
import com.nate.exception.InvalidTransactionAmount;
import com.nate.exception.UnauthorizedAccessException;
import com.nate.exception.UserNotLoggedInException;
import com.nate.model.IAccount;
import com.nate.util.CurrencyFormatter;
import com.nate.util.FindAccount;
import com.nate.util.OwnershipValidation;

import java.math.BigDecimal;
import java.sql.SQLException;


public class BankingService implements IBankingService{

    private final ISessionService service;
    private final ITransactionDao transactionDao;
    private final IAccountDao accountDao;
    private final UserRepo userRepo;
    private final FindAccount findAccount;
    private final OwnershipValidation ownershipValidation;

    public BankingService(ISessionService service, ITransactionDao transactions, IAccountDao accountDao, UserRepo userRepo,FindAccount findAccount,OwnershipValidation ownershipValidation){
        this.service =service;
        this.transactionDao = transactions;
        this.accountDao = accountDao;
        this.userRepo = userRepo;
        this.ownershipValidation = ownershipValidation;
        this.findAccount = findAccount;
    }

    // Opens a new account with the specified amount and type for the logged-in user
    @Override
    public void openAccount(BigDecimal amount, String type){
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        IAccountService accountService = new AccountService(service,accountDao,transactionDao,userRepo, findAccount,ownershipValidation);

        type = type.trim();
       if(amount.compareTo(BigDecimal.ZERO)>=0 && !type.isEmpty()) {
          IAccount acc = accountService.createAccount(amount, type,service.getCurrentUser());
           System.out.println("Account created successfully!");

           System.out.println(acc);
       }
       else if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidTransactionAmount(amount);
       }

       else{
           throw new IllegalArgumentException("Account type was not specified");
       }
    }


    // Displays the current balance of a given account
    @Override
    public boolean viewBalance(String accountNumber) throws SQLException {
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        IAccount account = findAccount.findUserAccount(accountNumber);

        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }



       System.out.println("Current balance for account " + account.getAccountNumber() + ": " + CurrencyFormatter.getCurrency(account.getBalance()));
        return true;
    }

}
