package Test.service;

import com.nate.database.*;
import com.nate.exception.AccountNotFoundException;
import com.nate.exception.InvalidTransactionAmount;
import com.nate.exception.UnauthorizedAccessException;
import com.nate.exception.UserNotLoggedInException;
import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.service.BankingService;
import com.nate.service.SessionService;
import com.nate.util.FindAccount;
import com.nate.util.OwnershipValidation;
import com.nate.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankingServiceTest {
    @Mock
    SessionService mockSession;
    @Mock
    UserRepo mockRepo;
    @Mock
    TransactionDao mockTransDao;
    @Mock
    AccountDao mockAccountDao;
    @Mock
    FindAccount mockFindAccount;
    @Mock
    OwnershipValidation mockOwnershipValidation;
    @InjectMocks
    BankingService bankingService;

    private IUser mockUser;
    private final PasswordUtil passwordUtil = new PasswordUtil();

    @BeforeEach
    void startUp(){
        String salt = passwordUtil.generateSalt();
        String hash = passwordUtil.hash("tester1",salt);
        mockUser = new User("Test",hash,"tester",salt);
    }

   @DisplayName("Open Account Test")
   @Nested
   class OpenAccount {
        @DisplayName("Open Account Test: Open account successfully")
       @Test
       void testOpenAccount_Successfully() {
           when(mockSession.isLoggedIn()).thenReturn(true);
           when(mockSession.getCurrentUser()).thenReturn(mockUser);

           assertDoesNotThrow(() -> bankingService.openAccount(new BigDecimal("100.00"), "savings"));
       }

       @DisplayName("Open Account Test: Fail open account user not logged in")
       @Test
       void testOpenAccount_UserNotLoggedInShouldThrowException(){
            when(mockSession.isLoggedIn()).thenReturn(false);

            assertThrows(UserNotLoggedInException.class,()->{
                bankingService.openAccount(new BigDecimal("0.00"),"savings");
            });
       }

       @DisplayName("Open Account Test: Fail open account, amount specified is less than 0")
       @Test
       void testOpenAccount_AmountSpecifiedIsNegative(){
            when(mockSession.isLoggedIn()).thenReturn(true);

            assertThrows(InvalidTransactionAmount.class, ()->{
               bankingService.openAccount(new BigDecimal("-100.00"),"savings");
            });
       }

       @DisplayName("Open Account Test: Fail open account, account type not specified")
       @Test
       void testOpenAccount_AccountTypeNotSpecified(){
            when(mockSession.isLoggedIn()).thenReturn(true);

            assertThrows(Exception.class,()->{
                bankingService.openAccount(new BigDecimal("0.00"),"");
            });
       }
   }

   @DisplayName("View Balance Test")
    @Nested
    class ViewBalance{
        @DisplayName("View Balance Test: Successfully View Test")
       @Test
       void testViewBalance_Successfully() throws SQLException {
            when(mockSession.isLoggedIn()).thenReturn(true);

            IAccount account = new Account(new BigDecimal("100"),"savings",mockUser.getUsername(),"8675432617");

            when(mockOwnershipValidation.isOwner(account.getAccountNumber())).thenReturn(true);
            when(mockFindAccount.findUserAccount("8675432617")).thenReturn(account);

            boolean check = bankingService.viewBalance(account.getAccountNumber());
            assertTrue(check);

        }

        @DisplayName("View Balance Test: Fail to view balance, user not logged in")
       @Test
       void testViewBalance_UnsuccessfullyUserNotLoggedIn(){
            when(mockSession.isLoggedIn()).thenReturn(false);

            assertThrows(UserNotLoggedInException.class,()->{
                bankingService.viewBalance("7463526354");
            });
        }

        @DisplayName("View Balance Test: Fail to view balance, unauthorized Access")
       @Test
       void testViewBalance_UnsuccessfullyUnauthorizedAccess() throws SQLException {
            when(mockSession.isLoggedIn()).thenReturn(true);

            IAccount account = new Account(new BigDecimal("100"),"savings",mockUser.getUsername(),"8675432617");

            when(mockOwnershipValidation.isOwner(account.getAccountNumber())).thenReturn(false);
            when(mockFindAccount.findUserAccount("8675432617")).thenReturn(account);

            assertThrows(UnauthorizedAccessException.class,()->{
                bankingService.viewBalance(account.getAccountNumber());
            });
        }


        @DisplayName("View Balance Test: Fail tp view balance, account not found")
       @Test
       void testViewBalance_UnsuccessfullyAccountNotFound() throws SQLException {
            when(mockSession.isLoggedIn()).thenReturn(true);

            when(mockFindAccount.findUserAccount("1234567890")).thenThrow(new AccountNotFoundException("1234567890"));

            assertThrows(AccountNotFoundException.class,()->{
                bankingService.viewBalance("1234567890");
            });
        }
   }
}
