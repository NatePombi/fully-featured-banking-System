package Test.util;

import com.nate.database.AccountDao;
import com.nate.database.TransactionDao;
import com.nate.exception.AccountNotFoundException;
import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.util.FindAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindAccountTest {

    @Mock
    AccountDao mockAccountDao;
    @Mock
    TransactionDao transactionDao;


    private FindAccount findAccount;

    @BeforeEach
    void startUp(){
        findAccount = new FindAccount(transactionDao,mockAccountDao);
    }


    @Test
    void testFindUserAccount() throws SQLException {
        IAccount account = mock(Account.class);
        when(account.getAccountNumber()).thenReturn("1234567890");

        List<IAccount> accounts = List.of(account);
        when(mockAccountDao.getAll()).thenReturn(accounts);

        IAccount result = findAccount.findUserAccount("1234567890");

        assertEquals("1234567890",result.getAccountNumber(),"should have the same account number");
    }

    @Test
    void testFindUserAccount_AccountNotFoundShouldThrowException(){
        assertThrows(AccountNotFoundException.class,()->{
            findAccount.findUserAccount("1234567890");
        });
    }
}
