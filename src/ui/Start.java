package ui;

import database.UserRepo;
import exception.*;
import model.IAccount;
import model.ITransactions;
import service.*;
import util.CurrencyFormatter;
import util.DateTimeFormat;
import util.ExceptionHandler;
import util.NoAccountPresent;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static ui.InputHandler.*;
import static ui.InputHandler.promptAccountNumber;
import static ui.InputHandler.promptAccountNumberReceivingFunds;
import static ui.InputHandler.promptAccountNumberSendingFunds;
import static ui.InputHandler.promptAmount;
import static ui.InputHandler.promptPassword;
import static ui.InputHandler.promptUserName;
import static ui.Processing.processing;
import static ui.SystemMessage.*;
import static ui.SystemMessage.showHeader;

/**
 * Entry point for the banking application UI.
 * Handles both pre-login and post-login menus and logic.
 */
public class Start {
    private static boolean running = true;

    private final IAccountService accountService;
    private final UserRepo userRepo;
    private final IUserService userService;
    private final IBankingService bankingService;
    private  final ISessionService service;
    private final MenuRenderer menuRenderer;
    private final ExceptionHandler exceptionHandler;

    public Start(IUserService userService,ISessionService service, IAccountService accountService, UserRepo userRepo, IBankingService bankingService,MenuRenderer menuRenderer){
        this.service = service;
        this.accountService = accountService;
        this.userRepo = userRepo;
        this.menuRenderer = menuRenderer;
        this.userService =userService;
        this.bankingService = bankingService;
        this.exceptionHandler = new ExceptionHandler();
    }

    // Starts the main application loop
    public void start() throws InterruptedException, SQLException {
        int input;
        while(running) {

            // Show startup menu if no user is logged in
            if (!service.isLoggedIn()) {
                menuRenderer.showStartUpMenu();
                input = promptMenuSelection();  // Get user input for start menu
                handleStartUpMenu(input);
            } else {

                // Show banking menu if a user is logged in
                service.setCurrentUser(userRepo.reloadUser(service.getCurrentUser().getUsername()));
                menuRenderer.showBankingMenu();
                input = promptMenuSelection();  // Get user input for banking menu
                handleBankingMenu(input);
            }
        }

    }


    // Handles user choices from the startup menu
    private void handleStartUpMenu(int input) throws InterruptedException {


        switch (input){
            case 1-> registerU();
            case 2-> logInUser();
            case 3-> {
                showGoodBye();
                running = false;
            }
        }
    }

    // Handles user registration
    private void registerU(){


        ui.SystemMessage.showHeader("Registering User");

        final String name = promptName();
        final String username = promptUserName();
        final String password =promptPassword();

        try {
            userService.registerUser(username, password, name);
        }
        catch (NullPointerException | IllegalArgumentException | SQLException ex){
            exceptionHandler.handleException(ex);
        }
    }

    // Handles user login
    private void logInUser(){
        ui.SystemMessage.showHeader("Log In");
        try {
            String username = promptUserName();

            String password = promptPassword();

            userService.loginUser(username, password);
        }
        catch(NullPointerException | IllegalArgumentException | NoSuchElementException | SQLException ex){
            exceptionHandler.handleException(ex);
        }
    }

    // Handles user choices from the banking menu
    private void handleBankingMenu(int input) throws InterruptedException, SQLException {

        switch (input){
            case 1 -> openAccount();
            case 2 -> viewAccountBalance();
            case 3 -> depositFunds();
            case 4 -> withdrawFunds();
            case 5 -> transferFundsToDifferentAccount();
            case 6 -> transactionHistory();
            case 7 -> accountDetails();
            case 8 -> viewAllAccounts();
            case 9 -> deleteAccount();
            case 10 -> {
                showLogOut();
                //PersistenceService.saveAll();
                service.logOut();
            }
            default -> System.out.println("Invalid selection. Please choose a number from the menu.");

        }
    }

    // Creates a new account for the logged-in user
    private void openAccount(){
        ui.SystemMessage.showHeader("Creating Account");


        try {
            String type = emptyInputOrSpellingError();
            accountService.createAccount(BigDecimal.ZERO, type, service.getCurrentUser());
        }
        catch (IllegalArgumentException | UserNotLoggedInException | NullPointerException e){
            exceptionHandler.handleException(e);
        }
    }

    // Displays account balance
    private void viewAccountBalance() {


           try {

               if (!NoAccountPresent.noAccount()) {
                   throw new NoAccountException();
               }

               String accountNumber = promptAccountNumber();

               bankingService.viewBalance(accountNumber);
           } catch (UnauthorizedAccessException | AccountNotFoundException | UserNotLoggedInException |
                    NoAccountException | SQLException e) {
               exceptionHandler.handleException(e);
           }


    }

    // Deposits money into an account
    private void depositFunds() throws InterruptedException {


        try {
            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            String accountNumber = promptAccountNumber();


            BigDecimal amount = promptAmount("Enter the amount you want to deposit: R");

            processing();
            System.out.println();

            accountService.deposit(accountNumber, amount);
        }

        catch (UnauthorizedAccessException | AccountNotFoundException | InvalidTransactionAmount |
               UserNotLoggedInException | NoAccountException | SQLException | NullPointerException ex){
            exceptionHandler.handleException(ex);
        }


    }

    // Withdraws money from an account
    private void withdrawFunds() throws InterruptedException {

        try {
            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            String accountNumber = promptAccountNumber();

            BigDecimal amount = promptAmount("Enter the amount you want to withdraw: R");

            processing();
            System.out.println();

            accountService.withdraw(accountNumber, amount);
        }
        catch (AccountNotFoundException | UnauthorizedAccessException | InvalidTransactionAmount |
               UserNotLoggedInException | NoAccountException | SQLException | InsufficientFunds | NullPointerException ex){
                exceptionHandler.handleException(ex);
        }

    }

    // Displays transaction history
    private void transactionHistory() throws InterruptedException {

        try {
            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            String accountNumber = promptAccountNumber();

            processing();
            System.out.println();

            System.out.println("""
                           Transactions:
                      Account Number: %s
                      --------------------------------
                    """.formatted(accountNumber));

            for(ITransactions t : accountService.getTransactions(accountNumber)){
                System.out.printf("Account: %-10s | %-10s | Amount: %-10s  |  Date: %s \n",t.getAccountNumber(), t.getDescription(), CurrencyFormatter.getCurrency(t.getAmount()), DateTimeFormat.dateFormatter(t.getTimeStamp()));
            }


        }
        catch (AccountNotFoundException | UnauthorizedAccessException | UserNotLoggedInException | NoAccountException |
               SQLException ex){
            exceptionHandler.handleException(ex);
        }
    }

    // Displays account details
    private void accountDetails() throws InterruptedException {

        try {

            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            String accountNumber = promptAccountNumber();

            processing();
            System.out.println();

            System.out.println(accountService.getAccountDetails(accountNumber));
        }
        catch (UnauthorizedAccessException | AccountNotFoundException | UserNotLoggedInException | NoAccountException |
               SQLException ex){
            exceptionHandler.handleException(ex);
        }
    }

    // Deletes an account
    private void deleteAccount() throws InterruptedException {


        try {
            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            showHeader("\nDeleting Account");

            String accountNumber = promptAccountNumber();

            processing();
            System.out.println();

            accountService.deleteAccount(accountNumber);
        }
        catch (UnauthorizedAccessException | AccountNotFoundException | DeletingWithFundsException |
               UserNotLoggedInException | NoAccountException | SQLException ex){
                exceptionHandler.handleException(ex);
        }
    }

    // Transfers funds between two accounts
    private void transferFundsToDifferentAccount() throws InterruptedException {
        try {
            if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
            }

            showHeader("\nTransferring Funds");

            String accountNumberFrom = promptAccountNumberSendingFunds();

            String accountNumberTo = promptAccountNumberReceivingFunds();

            BigDecimal amount = promptAmount("Enter amount you want to transfer: R");

            processing();
            System.out.println();


            accountService.transfer(accountNumberFrom, accountNumberTo, amount);
        }
        catch (UnauthorizedAccessException | AccountNotFoundException | InvalidTransactionAmount |
               InvalidTransferAmountFrom | UserNotLoggedInException | NoAccountException | SQLException | NullPointerException | InsufficientFunds e){
            exceptionHandler.handleException(e);
        }

    }

    public void viewAllAccounts() throws SQLException {
       try {
           if (!NoAccountPresent.noAccount()) {
                throw new NoAccountException();
           }
           List<IAccount> accounts = accountService.getAllUserAccounts();
           for (IAccount account : accounts) {
               System.out.printf("Account Owner: %-10s | Account Number: %-10s | Account type: %s | Balance: R%s\n",
                                        account.getOwnerUsername(),account.getAccountNumber(),account.getType(),account.getBalance());
           }
       }
       catch (NoAccountException | SQLException e){
           exceptionHandler.handleException(e);
       }

    }
}


