package service;

import Test.service.UserServiceTest;
import database.*;
import model.*;
import repository.Config;
import util.FindAccount;
import util.NegOrZeroCheck;
import util.OwnershipValidation;
import exception.*;



import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AccountService implements IAccountService{


    private final UserRepo userRepo;
    private final ITransactionDao transactionDao;
    private final IAccountDao accountDao;
    private final ISessionService service;
    private final FindAccount findAccount;
    private final OwnershipValidation ownershipValidation;


    public AccountService(ISessionService service, IAccountDao accountDao, ITransactionDao transactionDao,UserRepo userRepo,FindAccount findAccount, OwnershipValidation ownershipValidation){
        this.service = service;
        this.transactionDao =transactionDao;
        this.accountDao = accountDao;
        this.userRepo = userRepo;
        this.findAccount = findAccount;
        this.ownershipValidation = ownershipValidation;
    }

    // Creates a new account and associates it with the logged-in user
    @Override
    public IAccount createAccount(BigDecimal balance, String type, IUser user){

        if(service.getCurrentUser()==null){
            throw new UserNotLoggedInException();
        }

        IAccount acc = new Account(balance,type, user.getUsername());
        user.addAccount(acc);


        try {
            accountDao.addAccount(acc);
            service.setCurrentUser(userRepo.reloadUser(user.getUsername()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("You have successfully created a " + acc.getType() + " with an account number " + acc.getAccountNumber() + ".");
        return acc;
    }

    // Deposits funds into a specified account
    @Override
    public void deposit(String accountNumber,BigDecimal amount) throws SQLException {
        IUser user = service.getCurrentUser();

        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        if(amount == null){
            throw new NullPointerException();
        }

        if(NegOrZeroCheck.isNegativeOrZero(amount)){
            throw new InvalidTransactionAmount(amount);
        }


            IAccount acc =findAccount.findUserAccount(accountNumber);

        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }


        acc.deposit(amount);
        accountDao.updateAccountBalanceDeposit(accountNumber,amount,user);
        service.setCurrentUser(userRepo.reloadUser(user.getUsername()));
        System.out.println("Successfully deposited");



    }

    // Withdraws funds from a specified account
    @Override
    public  void withdraw(String accountNumber, BigDecimal amount) throws SQLException {
        IUser user = service.getCurrentUser();
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        if (amount == null){
            throw new NullPointerException();
        }

        if(NegOrZeroCheck.isNegativeOrZero(amount)){
            throw new InvalidTransactionAmount(amount);
        }


        IAccount account = findAccount.findUserAccount(accountNumber);


        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }

        if(account.getBalance().compareTo(amount)<0){
            throw new InsufficientFunds(account);
        }



        account.withdraw(amount);
       accountDao.updateAccountBalanceWithdraw(accountNumber,amount,user);
        service.setCurrentUser(userRepo.reloadUser(user.getUsername()));
        System.out.println("Withdrawal was successful");
    }

    // Retrieves details of a specific account
    @Override
    public IAccount getAccountDetails(String accountNumber) throws SQLException {
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }


        IAccount acc = findAccount.findUserAccount(accountNumber);

        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }



        return acc;
    }

    // Returns the list of transactions for a given account
    @Override
    public List<ITransactions> getTransactions(String accountNumber) throws SQLException {
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }
        findAccount.findUserAccount(accountNumber);

        if(!ownershipValidation.isOwner(accountNumber)){
            throw new UnauthorizedAccessException(accountNumber);
        }

        return transactionDao.getTransactionsByAccountNumber(accountNumber);
    }

    // Deletes an account if it belongs to the current user and has zero balance
    @Override
    public  void deleteAccount(String accountNumber) throws SQLException {
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        IUser currentUser = service.getCurrentUser();

        IAccount accountToDelete = findAccount.findUserAccount(accountNumber);

        if(!accountToDelete.getOwnerUsername().equals(currentUser.getUsername())){
           throw new UnauthorizedAccessException(accountNumber);
       }

        if (accountToDelete.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new DeletingWithFundsException(accountToDelete.getBalance(), accountNumber);
        }



        accountDao.deleteAccountByAccountNumber(accountNumber);
        System.out.println("Account with number " + accountNumber + " has been successfully deleted");
    }

    // Transfers funds between two accounts
    @Override
    public  void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws SQLException {
        if (!service.isLoggedIn()) {
            throw new UserNotLoggedInException();
        }

        IAccount accountFrom = findAccount.findUserAccount(fromAccountNumber);
        if (accountFrom == null) {
            throw new AccountNotFoundException("From account not found: " + fromAccountNumber);
        }
        if (!ownershipValidation.isOwner(fromAccountNumber)) {
            throw new UnauthorizedAccessException(fromAccountNumber);
        }

        IAccount accountTo = findAccount.findUserAccount(toAccountNumber);
        if (accountTo == null) {
            throw new AccountNotFoundException("To account not found: " + toAccountNumber);
        }


        if (NegOrZeroCheck.isNegativeOrZero(amount)) {
            throw new InvalidTransactionAmount(amount);
        }


        if (NegOrZeroCheck.isNegative(accountFrom.getBalance(), amount)) {
            throw new InvalidTransferAmountFrom(amount);
        }

        if(accountFrom == accountTo){
            throw new TransferToSameAccountException();
        }


        BigDecimal newFrom = accountFrom.getBalance().subtract(amount);
        BigDecimal newTo = accountTo.getBalance().add(amount);

        accountDao.updateAccountBalanceTransfer(fromAccountNumber,toAccountNumber,newFrom,newTo,amount);
        System.out.println("Transfer successful");

    }


    @Override
    public List<IAccount> getAllUserAccounts() throws SQLException {
        if(!service.isLoggedIn()){
            throw new UserNotLoggedInException();
        }

        List<IAccount> accounts = accountDao.getAllAccountsByUsername(service.getCurrentUser().getUsername());

        return accounts;
    }


}
