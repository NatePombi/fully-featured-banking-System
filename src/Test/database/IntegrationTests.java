package Test.database;

import database.UserDao;
import database.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class IntegrationTests {

    private ITransactionDao transactionDao;
    private IAccountDao accountDao;
    private IUserDao userDao;
    private UserRepo userRepo;

    private IAccount testAccount;
    private IUser testUser;
    private PasswordUtil passwordUtil;



    @BeforeEach
    public void startUp() throws SQLException {
        dbFunction.databaseInit();
        this.transactionDao = new TransactionDao();
        this.accountDao = new AccountDao(transactionDao);
        this.userDao = new UserDao();
        this.passwordUtil = new PasswordUtil();

        String salt = passwordUtil.generateSalt();
        String passwordHash = passwordUtil.hash("test123",salt);
        this.testUser = new User("Test1",passwordHash,"Test One", salt);
        userDao.addUser(testUser);

        this.testAccount = new Account(new BigDecimal("2000.00"), AccountType.SAVINGS.toString(),"Test1");
        accountDao.addAccount(testAccount);
    }


    @DisplayName("Deposit and Withdraw Test")
    @Test
    void testDepositAndWithdraw() throws SQLException {

        accountDao.updateAccountBalanceDeposit(testAccount.getAccountNumber(),new BigDecimal("1000.00"), testUser);
        accountDao.updateAccountBalanceWithdraw(testAccount.getAccountNumber(),new BigDecimal("500.00"), testUser);

        List<IAccount> accounts = accountDao.getAllAccountsByUsername(testUser.getUsername());
        assertEquals(1, accounts.size(),"expecting 1 account to be in the list");
        assertEquals(new BigDecimal("2500.00"), accounts.get(0).getBalance(),"Expecting 2500 to be the balance after both transactions has been done");
    }

    @DisplayName("Transfer between Account Test")
    @Test
    void testTransferBetweenAccounts() throws SQLException {
        IAccount account2 = new Account(new BigDecimal("100"),AccountType.CHECKING.toString(),"Test1");
        accountDao.addAccount(account2);
        BigDecimal transferAmount = new BigDecimal("1000.00");
        BigDecimal newFrom = testAccount.getBalance().subtract(transferAmount);
        BigDecimal newTo = account2.getBalance().add(transferAmount);

        accountDao.updateAccountBalanceTransfer(testAccount.getAccountNumber(),account2.getAccountNumber(),newFrom,newTo,transferAmount);

        List<IAccount> accounts = accountDao.getAllAccountsByUsername(testUser.getUsername());
        assertEquals(2,accounts.size(),"Expecting user to have 2 active accounts");
        assertEquals(0,accounts.get(0).getBalance().compareTo(new BigDecimal("1000.00")),"Expecting account 1(the sender) to have a remaining balance of 1000");
        assertEquals(0,accounts.get(1).getBalance().compareTo(new BigDecimal("1100.00")),"Expecting account 2(the receiver to have a balance of 1100 after the transfer");

    }

    @DisplayName("Transaction History Test")
    @Test
    void testTransactionHistory() throws SQLException {
        accountDao.updateAccountBalanceDeposit(testAccount.getAccountNumber(),new BigDecimal("1000.00"), testUser);
        accountDao.updateAccountBalanceWithdraw(testAccount.getAccountNumber(),new BigDecimal("500.00"), testUser);

        List<ITransactions> transactions = transactionDao.getTransactionsByAccountNumber(testAccount.getAccountNumber());
        assertFalse(transactions.isEmpty(),"Expecting the list not to be empty");

    }

    @AfterEach
    public void reset() throws SQLException {
        try(var conn = dbFunction.getConnection();
            Statement stm = conn.createStatement()
        ){
            //Clearing the database once methods has been run
            stm.execute("DELETE FROM users");
            stm.execute("DELETE FROM accounts");
            stm.execute("DELETE FROM transactions");
        }
    }
}

