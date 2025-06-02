package app;

import database.*;
import service.*;
import ui.MenuRenderer;
import ui.Start;
import util.FindAccount;
import util.OwnershipValidation;

public class Bootstrap {
    public static Start init(){

        ITransactionDao transactionDao = new TransactionDao();
        IAccountDao accountDao = new AccountDao(transactionDao);
        UserRepo userRepo = new UserRepo();
        IUserDao userDao = new UserDao();
        ISessionService service = new SessionService();
        IUserService userService = new UserService(service,userDao);
        IBankingService bankingService = new BankingService(service,transactionDao,accountDao,userRepo);
        MenuRenderer menuRenderer = new MenuRenderer(service);
        FindAccount findAccount = new FindAccount();
        OwnershipValidation ownershipValidation = new OwnershipValidation(service);
        IAccountService accountService = new AccountService(service,accountDao,transactionDao,userRepo,findAccount,ownershipValidation);

        return new Start(userService,service,accountService,userRepo,bankingService,menuRenderer);
    }
}
