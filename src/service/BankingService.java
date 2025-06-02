package service;

import database.AccountDao;
import database.IAccountDao;
import database.ITransactionDao;
import database.UserRepo;
import exception.InvalidTransactionAmount;
import exception.UnauthorizedAccessException;
import exception.UserNotLoggedInException;
import model.IAccount;
import util.CurrencyFormatter;
import util.FindAccount;
import util.OwnershipValidation;

import java.math.BigDecimal;
import java.sql.SQLException;


public class BankingService implements IBankingService{

    private final ISessionService service;
    private final ITransactionDao transactionDao;
    private final IAccountDao accountDao;
    private final UserRepo userRepo;
    private FindAccount findAccount = new FindAccount();
    private final OwnershipValidation ownershipValidation;

    public BankingService(ISessionService service, ITransactionDao transactions, IAccountDao accountDao, UserRepo userRepo){
        this.service =service;
        this.transactionDao = transactions;
        this.accountDao = accountDao;
        this.userRepo = userRepo;
        this.ownershipValidation = new OwnershipValidation(service);
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
    public void viewBalance(String accountNumber) throws SQLException {
        AccountDao accountDao = new AccountDao(transactionDao);
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        IAccount account = findAccount.findUserAccount(accountNumber);

        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }



       System.out.println("Current balance for account " + account.getAccountNumber() + ": " + CurrencyFormatter.getCurrency(account.getBalance()));
    }

}
