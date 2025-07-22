package Test.ui;

import com.nate.database.*;
import com.nate.exception.*;
import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.service.*;
import com.nate.ui.InputHandler;
import com.nate.ui.MenuRenderer;
import com.nate.ui.Start;
import com.nate.ui.SystemMessage;
import com.nate.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartTest {
    @Mock
    PromptMessage message;
    @Mock
    NoAccountPresent noAccountPresent;
    @Mock
    IAccountService mockAccountService;
    @Mock
    ExceptionHandler exceptionHandler;
    @Mock
    InputHandler inputHandler;
    @Mock
    UserService mockUserService;
    @Mock
    SessionService mockSessionService;
    @Mock
    UserRepo mockUserRepo;
    @Mock
    BankingService mockBankingService;
    @Mock
    MenuRenderer mockMenu;

    //private ScannerError scannerError;
    private Start start;
    private ScannerError scannerError;


    @BeforeEach
    void startUp() {
        Scanner scanner = mock(Scanner.class);
        scannerError = new ScannerError(scanner);
        scannerError.scanner = scanner;
        PasswordUtil passwordUtil = new PasswordUtil();
        String salt = passwordUtil.generateSalt();
        String hash = passwordUtil.hash("test123", salt);
        start = Mockito.spy(new Start(mockUserService, mockSessionService, mockAccountService, mockUserRepo, mockBankingService, mockMenu, inputHandler, exceptionHandler, noAccountPresent,message));
    }

    @DisplayName("Register ui Test")
    @Nested
    class RegisterUI {
        @Test
        void testRegister() throws SQLException {
            when(inputHandler.promptName()).thenReturn("Test One");
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");


            assertDoesNotThrow(() -> {
                start.registerU();
            });

            verify(mockUserService).registerUser(eq("tester"), eq("test123"), eq("Test One"));
        }


        @DisplayName("Register ui Test: fail to register, user not found")
        @Test
        void testRegister_UserNotFoundShouldThrowException() throws SQLException {
            when(inputHandler.promptName()).thenReturn("Test One");
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new IllegalArgumentException("User not found")).when(mockUserService).registerUser(any(), any(), any());

            start.registerU();

            verify(exceptionHandler).handleException(any(IllegalArgumentException.class));
        }

        @DisplayName("Register ui Test: fail to register with null/empty fields")
        @Test
        void testRegister_failShouldThrowException() throws SQLException {
            when(inputHandler.promptName()).thenReturn("Test One");
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new NullPointerException("Null")).when(mockUserService).registerUser(any(), any(), any());

            start.registerU();

            verify(exceptionHandler).handleException(any(NullPointerException.class));
        }

        @DisplayName("Register ui Test: fail to register, SQL exception")
        @Test
        void testRegister_failShouldThrowSQLException() throws SQLException {
            when(inputHandler.promptName()).thenReturn("Test One");
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new SQLException("Invalid")).when(mockUserService).registerUser(any(), any(), any());

            start.registerU();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }
    }

    @DisplayName("Login ui Test")
    @Nested
    class LoginUI {
        @Test
        void testLogin() throws SQLException {
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            assertDoesNotThrow(() -> {
                start.logInUser();
            });

            verify(mockUserService).loginUser(eq("tester"), eq("test123"));
        }

        @DisplayName("Login ui Test: fail to login, null/empty fields")
        @Test
        void testLogin_failShouldThrowException() throws SQLException {
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new NullPointerException("Null")).when(mockUserService).loginUser(any(), any());

            start.logInUser();

            verify(exceptionHandler).handleException(any(NullPointerException.class));
        }


        @DisplayName("Login ui Test: fail to login, incorrect Password")
        @Test
        void testLogin_IncorrectPasswordShouldThrowException() throws SQLException {
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new IllegalArgumentException("Incorrect Password")).when(mockUserService).loginUser(any(), any());

            start.logInUser();

            verify(exceptionHandler).handleException(any(IllegalArgumentException.class));
        }


        @DisplayName("Login ui Test: fail to login, sql exception")
        @Test
        void testLogin_failShouldThrowSQLException() throws SQLException {
            when(inputHandler.promptUserName()).thenReturn("tester");
            when(inputHandler.promptPassword()).thenReturn("test123");

            doThrow(new SQLException("invalid")).when(mockUserService).loginUser(any(), any());

            start.logInUser();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }
    }

    @DisplayName("Open Account ui Test")
    @Nested
    class OpenAccountUI {
        @Test
        void testOpenAccount() {
            when(inputHandler.emptyInputOrSpellingError()).thenReturn("savings");

            assertDoesNotThrow(() -> {
                start.openAccount();
            });


            verify(mockAccountService).createAccount(any(), any(), any());
        }


        @DisplayName("Open Account ui Test: failed , not logged in")
        @Test
        void testOpenAccount_FailedNotLoggedIn() {
            when(inputHandler.emptyInputOrSpellingError()).thenReturn("savings");
            when(mockSessionService.getCurrentUser()).thenReturn(null);

            doThrow(new UserNotLoggedInException()).when(mockAccountService).createAccount(any(), any(), any());

            start.openAccount();

            verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
        }


        @DisplayName("Open Account ui Test: failed , invalid account type")
        @Test
        void testOpenAccount_FailedInvalidAccountType() {
            when(inputHandler.emptyInputOrSpellingError()).thenReturn("savings");

            doThrow(new IllegalArgumentException("Invalid Account Type")).when(mockAccountService).createAccount(any(), any(), any());

            start.openAccount();

            verify(exceptionHandler).handleException(any(IllegalArgumentException.class));
        }
    }


    @DisplayName("Deposit Test")
    @Nested
    class DepositUI {
        @Test
        void testDeposit() throws SQLException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            assertDoesNotThrow(() -> {
                start.depositFunds();
            });

            verify(mockAccountService).deposit(eq("1234567890"), eq(new BigDecimal("100.00")));
        }

        @DisplayName("Deposit Test: failed deposit, Unauthorized access")
        @Test
        void testDeposit_UnauthorizedAccessShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UnauthorizedAccessException("No Authorized")).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
        }


        @DisplayName("Deposit Test: failed deposit, User not logged in")
        @Test
        void testDeposit_UserNotLoggedInShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UserNotLoggedInException()).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
        }


        @DisplayName("Deposit Test: failed deposit, account not found")
        @Test
        void testDeposit_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new AccountNotFoundException("Account not found")).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }


        @DisplayName("Deposit Test: failed deposit, Unauthorized access")
        @Test
        void testDeposit_InvalidTransactionShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new InvalidTransactionAmount(new BigDecimal("100.00"))).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(InvalidTransactionAmount.class));
        }

        @DisplayName("Deposit Test: failed deposit, no amount deposited ")
        @Test
        void testDeposit_NoAmountShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new NullPointerException("No Amount deposited")).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(NullPointerException.class));
        }

        @DisplayName("Deposit Test: failed deposit, SQL error")
        @Test
        void testDeposit_SQLShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new SQLException("invalid")).when(mockAccountService).deposit(anyString(), any());

            start.depositFunds();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }
    }


    @DisplayName("View balance Test")
    @Nested
    class ViewBalanceUI {
        @Test
        void testViewBalance() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");

            assertDoesNotThrow(() -> {
                start.viewAccountBalance();
            });

            verify(mockBankingService).viewBalance("1234567890");
        }

        @DisplayName("View balance Test: Failed to view balance, Unauthorized Access")
        @Test
        void testViewBalance_UnauthorizedAccessShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(any())).thenReturn("1234567890");


            doThrow(new UnauthorizedAccessException("Unauthorized")).when(mockBankingService).viewBalance(anyString());

            start.viewAccountBalance();

            verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
        }

        @DisplayName("View balance Test: Failed to view balance, User not logged in")
        @Test
        void testViewBalance_UserNotLoggedInShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(any())).thenReturn("1234567890");

            doThrow(new UserNotLoggedInException()).when(mockBankingService).viewBalance(anyString());

            start.viewAccountBalance();

            verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
        }


        @DisplayName("View balance Test: Failed to view balance, Account not found")
        @Test
        void testViewBalance_AccountNOtFoundShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(any())).thenReturn("1234567890");

            doThrow(new AccountNotFoundException("Not found")).when(mockBankingService).viewBalance(anyString());

            start.viewAccountBalance();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }


        @DisplayName("View balance Test: Failed to view balance, SQL")
        @Test
        void testViewBalance_SQLShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(any())).thenReturn("1234567890");

            doThrow(new SQLException("invalid")).when(mockBankingService).viewBalance(anyString());

            start.viewAccountBalance();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }
    }


    @DisplayName("Withdraw Test")
    @Nested
    class WithdrawTestUI {
        @Test
        void testWithdraw() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));

            assertDoesNotThrow(() -> {
                start.withdrawFunds();
            });

            verify(mockAccountService).withdraw(eq("1234567890"), eq(new BigDecimal("150.00")));
        }

        @DisplayName("Withdraw Test: failed to withdraw, account not found")
        @Test
        void testWithdraw_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));

            doThrow(new AccountNotFoundException("account not found")).when(mockAccountService).withdraw(anyString(), any());

            start.withdrawFunds();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }


        @DisplayName("Withdraw Test: failed to withdraw, unauthorizedAccess")
        @Test
        void testWithdraw_UnauthorizedShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));;

            doThrow(new UnauthorizedAccessException("unauthorized access")).when(mockAccountService).withdraw(anyString(), any());

            start.withdrawFunds();

            verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
        }


        @DisplayName("Withdraw Test: failed to withdraw, insufficient funds")
        @Test
        void testWithdraw_InsufficientFundsShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));
            IAccount account = new Account(new BigDecimal("100"),"savings","tester");
            doThrow(new InsufficientFunds(account)).when(mockAccountService).withdraw(anyString(), any());

            start.withdrawFunds();

            verify(exceptionHandler).handleException(any(InsufficientFunds.class));
        }


        @DisplayName("Withdraw Test: failed to withdraw, invalid amount")
        @Test
        void testWithdraw_InvalidAmountShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));

            doThrow(new InvalidTransactionAmount(new BigDecimal("-7"))).when(mockAccountService).withdraw(anyString(), any());

            start.withdrawFunds();

            verify(exceptionHandler).handleException(any(InvalidTransactionAmount.class));
        }


        @DisplayName("Withdraw Test: failed to withdraw, SQL error")
        @Test
        void testWithdraw_SQLShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("150.00"));

            doThrow(new SQLException("SQL error")).when(mockAccountService).withdraw(anyString(), any());

            start.withdrawFunds();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }


    }


    @DisplayName("Transaction History Test")
    @Nested
    class TransactionHistoryUI {
        @Test
        void testTransactionHistory() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("1234567890");

            assertDoesNotThrow(() -> {
                start.transactionHistory();
            });

            verify(mockAccountService).getTransactions(eq("1234567890"));
        }
    }

    @DisplayName("Transaction History Test: failed to get Transaction history, Unauthorized access")
    @Test
    void testTransactionHistory_UnauthorizedAccessShouldThrowException() throws SQLException, InterruptedException {
        when(noAccountPresent.noAccount()).thenReturn(true);
        when(message.promptInt(anyString())).thenReturn("1234567890");

        doThrow(new UnauthorizedAccessException("unauthorized Access")).when(mockAccountService).getTransactions(any());

        start.transactionHistory();

        verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
    }

    @DisplayName("Transaction History Test: failed to get Transaction history, account not found")
    @Test
    void testTransactionHistory_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
        when(noAccountPresent.noAccount()).thenReturn(true);
        when(message.promptInt(anyString())).thenReturn("1234567890");

        doThrow(new AccountNotFoundException("Account Not found")).when(mockAccountService).getTransactions(any());

        start.transactionHistory();

        verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
    }


    @DisplayName("Transaction History Test: failed to get Transaction history, User not logged in")
    @Test
    void testTransactionHistory_UserNotLoggedInShouldThrowException() throws SQLException, InterruptedException {
        when(noAccountPresent.noAccount()).thenReturn(true);
        when(message.promptInt(anyString())).thenReturn("1234567890");

        doThrow(new UserNotLoggedInException()).when(mockAccountService).getTransactions(any());

        start.transactionHistory();

        verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
    }


    @DisplayName("Transfer Between Accounts Test")
    @Nested
    class TransferBetweenAccountsUI {
        @Test
        void testTransferBetweenAccounts() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(message.promptIntRec(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));

            assertDoesNotThrow(()->{
                start.transferFundsToDifferentAccount();
            });

            verify(mockAccountService).transfer(eq("0987654321"),eq("1234567890"),eq(new BigDecimal("100.00")));
        }


        @DisplayName("Transfer Between Accounts: Failed to transfer , invalid transfer amount")
        @Test
        void testTransferBetweenAccounts_InvalidTransferAmountShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new InvalidTransactionAmount(new BigDecimal("-200.00"))).when(mockAccountService).transfer(any(),any(),any());

            start.transferFundsToDifferentAccount();

            verify(exceptionHandler).handleException(any(InvalidTransactionAmount.class));
        }

        @DisplayName("Transfer Between Accounts: Failed to transfer , same account")
        @Test
        void testTransferBetweenAccounts_SameAccountShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(message.promptIntRec(any())).thenReturn("1234567890");
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));

            doThrow(new TransferToSameAccountException()).when(mockAccountService).transfer(any(),any(),any());

            start.transferFundsToDifferentAccount();

            verify(exceptionHandler).handleException(any(TransferToSameAccountException.class));
        }

        @DisplayName("Transfer Between Accounts: Failed to transfer , account not found")
        @Test
        void testTransferBetweenAccounts_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));

            doThrow(new AccountNotFoundException("account not found")).when(mockAccountService).transfer(any(),any(),any());

            start.transferFundsToDifferentAccount();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }

        @DisplayName("Transfer Between Accounts: Failed to transfer , SQL error")
        @Test
        void testTransferBetweenAccounts_SQLErrorShouldThrowException() throws SQLException, InterruptedException {
            when(noAccountPresent.noAccount()).thenReturn(true);
            when(message.promptBigDec(anyString())).thenReturn(new BigDecimal("100.00"));

            doThrow(new SQLException("invalid")).when(mockAccountService).transfer(any(),any(),any());

            start.transferFundsToDifferentAccount();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }
    }

    @DisplayName("View All Accounts Test")
    @Nested
    class ViewAllAccountsUI{
        @Test
        void testViewAllAccounts() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);

            assertDoesNotThrow(()->{
                start.viewAllAccounts();
            });

            verify(mockAccountService).getAllUserAccounts();
        }

        @DisplayName("View All Account Test: failed to view Accounts, No Accounts ")
        @Test
        void testViewAllAccounts_NoAccountsShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new NoAccountException()).when(mockAccountService).getAllUserAccounts();

            start.viewAllAccounts();

            verify(exceptionHandler).handleException(any(NoAccountException.class));
        }

        @DisplayName("View All Account Test: failed to view Accounts, SQL error ")
        @Test
        void testViewAllAccounts_SQLErrorShouldThrowException() throws SQLException {
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new SQLException()).when(mockAccountService).getAllUserAccounts();

            start.viewAllAccounts();

            verify(exceptionHandler).handleException(any(SQLException.class));
        }

    }

    @DisplayName("Account Details Test")
    @Nested
    class AccountDetails{
        @Test
        void testAccountDetails() throws SQLException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            assertDoesNotThrow(()->{
                start.accountDetails();
            });

            verify(mockAccountService).getAccountDetails(eq("0987654321"));
        }

        @DisplayName("Account Details Test: Failed to view details , unauthorized access")
        @Test
        void testViewAccountDetails_UnauthorizedAccessShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UnauthorizedAccessException("unauthorized Access")).when(mockAccountService).getAccountDetails(any());

            start.accountDetails();

            verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
        }

        @DisplayName("Account Details Test: Failed to view details , user not logged in")
        @Test
        void testViewAccountDetails_UserNotLoggedInShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UserNotLoggedInException()).when(mockAccountService).getAccountDetails(any());

            start.accountDetails();

            verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
        }

        @DisplayName("Account Details Test: Failed to view details , Account not found")
        @Test
        void testViewAccountDetails_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new AccountNotFoundException("account not found")).when(mockAccountService).getAccountDetails(any());

            start.accountDetails();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }
    }

    @DisplayName("Delete Account Test")
    @Nested
    class DeleteAccountTest{
        @Test
        void testDeleteAccount() throws SQLException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            assertDoesNotThrow(()->{
                start.deleteAccount();
            });

            verify(mockAccountService).deleteAccount("0987654321");
        }

        @DisplayName("Delete Account Test: Failed to delete account, unauthorized Access")
        @Test
        void testDeleteAccount_UnauthorizedAccessShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UnauthorizedAccessException("Unauthorized access")).when(mockAccountService).deleteAccount(any());

            start.deleteAccount();

            verify(exceptionHandler).handleException(any(UnauthorizedAccessException.class));
        }

        @DisplayName("Delete Account Test: Failed to delete account, Account not Found")
        @Test
        void testDeleteAccount_AccountNotFoundShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new AccountNotFoundException("account not found")).when(mockAccountService).deleteAccount(any());

            start.deleteAccount();

            verify(exceptionHandler).handleException(any(AccountNotFoundException.class));
        }


        @DisplayName("Delete Account Test: Failed to delete account, Not logged in")
        @Test
        void testDeleteAccount_NotLoggedInShouldThrowException() throws SQLException, InterruptedException {
            when(message.promptInt(anyString())).thenReturn("0987654321");
            when(noAccountPresent.noAccount()).thenReturn(true);

            doThrow(new UserNotLoggedInException()).when(mockAccountService).deleteAccount(any());

            start.deleteAccount();

            verify(exceptionHandler).handleException(any(UserNotLoggedInException.class));
        }
    }

    @DisplayName("Handles Start Menu Test")
    @Nested
    class HandlesStartMenuUI{
        @Test
        void testHandleMenuRegister() throws SQLException, InterruptedException {
            start.handleStartUpMenu(1);

            verify(start).registerU();
        }

        @Test
        void testHandleMenuLogin() throws SQLException, InterruptedException {
            start.handleStartUpMenu(2);

            verify(start).logInUser();
        }

    }

    @DisplayName("Handles Banking Menu Test")
    @Nested
    class HandlesBankingUI{
        @Test
        void testHandlesBankingOpenAccount() throws SQLException, InterruptedException {
            start.handleBankingMenu(1);

            verify(start).openAccount();
        }

        @Test
        void testHandlesBankingViewAccountBalance() throws SQLException, InterruptedException {
            start.handleBankingMenu(2);

            verify(start).viewAccountBalance();
        }

        @Test
        void testHandlesBankingDeposit() throws SQLException, InterruptedException {
            start.handleBankingMenu(3);

            verify(start).depositFunds();
        }

        @Test
        void testHandlesBankingWithdraw() throws SQLException, InterruptedException {
            start.handleBankingMenu(4);

            verify(start).withdrawFunds();
        }

        @Test
        void testHandlesBankingTransferToDifferentAccount() throws SQLException, InterruptedException {
            start.handleBankingMenu(5);

            verify(start).transferFundsToDifferentAccount();
        }

        @Test
        void testHandlesBankingTransactionHistory() throws SQLException, InterruptedException {
            start.handleBankingMenu(6);

            verify(start).transactionHistory();
        }

        @Test
        void testHandlesBankingAccountDetails() throws SQLException, InterruptedException {
            start.handleBankingMenu(7);

            verify(start).accountDetails();
        }

        @Test
        void testHandlesBankingViewAllAccounts() throws SQLException, InterruptedException {
            start.handleBankingMenu(8);

            verify(start).viewAllAccounts();
        }

        @Test
        void testHandlesBankingDeleteAccount() throws SQLException, InterruptedException {
            start.handleBankingMenu(9);

            verify(start).deleteAccount();
        }


    }
}
