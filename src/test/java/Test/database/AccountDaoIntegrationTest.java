package Test.database;

import com.nate.database.UserDao;
import com.nate.database.*;
import com.nate.exception.InsufficientFunds;
import com.nate.model.*;
import org.junit.jupiter.api.*;
import com.nate.util.PasswordUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountDaoIntegrationTest {
    @Mock
    TransactionDao mockTransaction;
    @InjectMocks
    AccountDao accountDao;



    private IAccount mockAccount;
    private IUser mockUser;
    private PasswordUtil passwordUtil = new PasswordUtil();
    @BeforeEach
    public void startUp() throws SQLException {
        UserDao userDao = new UserDao();
        Configuration.config = true;
        DBFunction.databaseInit();
        String salt = passwordUtil.generateSalt();
        String hash = passwordUtil.hash("tester",salt);
        mockUser = new User("tester",hash,"Test",salt);
        userDao.addUser(mockUser);
        mockAccount = new Account(new BigDecimal("100.00"),"savings",mockUser.getUsername());
    }

    @AfterEach
    public void resetDb() throws SQLException {
        try(var conn = DBFunction.getConnection();
            Statement stm = conn.createStatement()
        ){
            stm.execute("DELETE FROM transactions");
            stm.execute("DELETE FROM users");
            stm.execute("DELETE FROM accounts");
        }
    }


    @DisplayName("Adding Account Test")
    @Test
    void testAddingAccount() throws SQLException {
        accountDao.addAccount(mockAccount);

        List<IAccount> accounts = accountDao.getAll();
        assertEquals(1,accounts.size(),"Should have a size of 1");
        assertEquals("tester",accounts.get(0).getOwnerUsername(),"Owners username should be tester");
    }

    @DisplayName("Get all Account Test")
    @Test
    void testGetAllAccount() throws SQLException {
        IAccount account = new Account(new BigDecimal("100.00"),"checking",mockUser.getUsername());
        accountDao.addAccount(mockAccount);
        accountDao.addAccount(account);


        List<IAccount> accounts = accountDao.getAll();

        assertNotNull(account);
        assertEquals(2,accounts.size());
    }


    @DisplayName("Update Balance after Transfer between accounts test")
    @Nested
    class TransferBetweenAccounts {
        @Test
        void testTransferBetweenAccounts() throws SQLException {
            IAccount account1 = new Account(new BigDecimal("100.00"), "checking", mockUser.getUsername());
            accountDao.addAccount(mockAccount);
            accountDao.addAccount(account1);

            boolean check = accountDao.updateAccountBalanceTransfer(mockAccount.getAccountNumber(), account1.getAccountNumber(), new BigDecimal("60.00"));

            assertTrue(check);

            List<IAccount> accounts = accountDao.getAll();

            for (IAccount acc : accounts) {
                if (acc.getAccountNumber().equals(mockAccount.getAccountNumber())) {
                    assertEquals(new BigDecimal("40.00"), acc.getBalance(), "mockAccount should have a balance of 40.00");
                }

                if (acc.getAccountNumber().equals(account1.getAccountNumber())) {
                    assertEquals(new BigDecimal("160.00"), acc.getBalance(), "account1 should have a balance of 160.00");
                }
            }

        }

        @Test
        void testTransferBetweenAccounts_failInsufficientFunds() throws SQLException {
            IAccount account1 = new Account(new BigDecimal("100.00"), "checking", mockUser.getUsername());
            accountDao.addAccount(mockAccount);
            accountDao.addAccount(account1);

            assertThrows(InsufficientFunds.class,()->{
                accountDao.updateAccountBalanceTransfer(mockAccount.getAccountNumber(),account1.getAccountNumber(),new BigDecimal("200.00"));
            });
        }
    }

    @DisplayName("Deposit Test")
    @Test
    void testDeposit() throws SQLException {
        accountDao.addAccount(mockAccount);

        assertDoesNotThrow(()->{
            accountDao.updateAccountBalanceDeposit(mockAccount.getAccountNumber(),new BigDecimal("100.00"),mockUser);
        });

        IAccount updatedAccount = accountDao.getAccountsByAccountNumber(mockAccount.getAccountNumber());
        assertEquals(new BigDecimal("200.00"),updatedAccount.getBalance(),"balance should be 200.00");
    }


    @DisplayName("Withdraw Test")
    @Test
    void testWithdraw() throws SQLException {
        accountDao.addAccount(mockAccount);

        assertDoesNotThrow(()->{
            accountDao.updateAccountBalanceWithdraw(mockAccount.getAccountNumber(),new BigDecimal("20.00"),mockUser);
        });

        IAccount updatedAccount = accountDao.getAccountsByAccountNumber(mockAccount.getAccountNumber());
        assertEquals(new BigDecimal("80.00"),updatedAccount.getBalance(),"balance should be 80.00");
    }

    @DisplayName("Get All Accounts By Username")
    @Test
    void testGetAllAccountsByUsername() throws SQLException {
        IAccount account1 = new Account(new BigDecimal("100.00"), "savings", mockUser.getUsername());
        accountDao.addAccount(mockAccount);
        accountDao.addAccount(account1);

        List<IAccount> accounts = accountDao.getAllAccountsByUsername(mockUser.getUsername());

        assertNotNull(accounts);
        assertEquals(2,accounts.size(),"should be 2 accounts present");
    }

    @DisplayName("Delete account Test")
    @Test
    void testDeleteAccount() throws SQLException {
        IAccount account1 = new Account(new BigDecimal("0.00"), "savings", mockUser.getUsername());
        accountDao.addAccount(mockAccount);
        accountDao.addAccount(account1);

        assertDoesNotThrow(()->{
            accountDao.deleteAccountByAccountNumber(account1.getAccountNumber());
        });

        List<IAccount> accounts = accountDao.getAll();

        assertEquals(1,accounts.size(),"Should be 1 since account1 got deleted");
    }
}
