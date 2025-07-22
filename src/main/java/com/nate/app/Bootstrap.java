package com.nate.app;

import com.nate.database.*;
import com.nate.service.*;
import com.nate.ui.InputHandler;
import com.nate.ui.MenuRenderer;
import com.nate.ui.Start;
import com.nate.util.*;

import java.util.Scanner;

public class Bootstrap {
    public static Start init(){

        ExceptionHandler exceptionHandler = new ExceptionHandler();
        Scanner scanner = new Scanner(System.in);
        ScannerError scannerError = new ScannerError(scanner);
        PromptMessage promptMessage = new PromptMessage(scannerError);
        InputHandler inputHandler = new InputHandler(scanner,scannerError);
        ITransactionDao transactionDao = new TransactionDao();
        IAccountDao accountDao = new AccountDao(transactionDao);
        UserRepo userRepo = new UserRepo();
        IUserDao userDao = new UserDao();
        ISessionService service = new SessionService();
        IUserService userService = new UserService(service,userDao);
        MenuRenderer menuRenderer = new MenuRenderer(service);
        FindAccount findAccount = new FindAccount(transactionDao,accountDao);
        NoAccountPresent noAccountPresent = new NoAccountPresent(service);
        OwnershipValidation ownershipValidation = new OwnershipValidation(service);
        IAccountService accountService = new AccountService(service,accountDao,transactionDao,userRepo,findAccount,ownershipValidation);
        IBankingService bankingService = new BankingService(service,transactionDao,accountDao,userRepo,findAccount,ownershipValidation);

        return new Start(userService,service,accountService,userRepo,bankingService,menuRenderer,inputHandler,exceptionHandler,noAccountPresent,promptMessage);
    }
}
