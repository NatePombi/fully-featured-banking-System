package Test.database;

import database.UserDao;
import database.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountDaoIntegrationTest {

    private IAccountDao accountDao;
    private ITransactionDao transactionDao;
    private IUserDao userDao;
    private PasswordUtil passwordUtil;

    @BeforeEach
    public void startUp(){
        dbFunction.databaseInit();
        this.userDao = new UserDao();
        this.transactionDao = new TransactionDao();
        this.accountDao = new AccountDao(transactionDao);
        this.passwordUtil = new PasswordUtil();
    }

    @AfterEach
    public void resetDb() throws SQLException {
        try(var conn = dbFunction.getConnection();
            Statement stm = conn.createStatement()
        ){
            stm.execute("DELETE FROM transactions");
            stm.execute("DELETE FROM users");
            stm.execute("DELETE FROM accounts");
        }
    }


    @Test
    void testCreatingUserAndAccounts() throws SQLException {
        //Creating User then storing it in the database
        IUser user = new User("Test2","test123","Test One", passwordUtil.generateSalt());
        userDao.addUser(user);

        //Creating an account and then storing it in the database
        IAccount account = new Account(new BigDecimal("1000.00"), AccountType.CHECKING.toString(),"Test2");
        accountDao.addAccount(account);

        //retrieve
        List<IAccount> accounts = accountDao.getAllAccountsByUsername(user.getUsername());

        assertEquals(1,accounts.size());
        IAccount accountRetrieved = accounts.get(0);
        assertEquals(user.getUsername(),accountRetrieved.getOwnerUsername());
        assertEquals(0,accountRetrieved.getBalance().compareTo(new BigDecimal("1000.00")));
        assertEquals("CHECKING",accountRetrieved.getType().toString());


    }
}
