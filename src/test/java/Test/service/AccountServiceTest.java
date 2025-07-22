package Test.service;

import com.nate.database.*;
import com.nate.exception.*;
import com.nate.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nate.service.*;
import com.nate.util.FindAccount;
import com.nate.util.OwnershipValidation;
import com.nate.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
     @Mock
    private AccountDao mockAccountDao;
     @Mock
    private TransactionDao mockTransactionDao;
     @Mock
    private SessionService mockService;

     @Mock
    private UserRepo mockUserepo;

     @Mock
     private FindAccount mockFindAccount;

     @Mock
     private OwnershipValidation mockOwnershipValidation;

     @InjectMocks
    private AccountService accountService;

    private Account mockAccount;
    private User mockUser;
    private PasswordUtil passwordUtil = new PasswordUtil();


        @Captor
        private ArgumentCaptor<IUser> iuserArgumentCaptor;

        @Captor ArgumentCaptor<ITransactions> transactionsArgumentCaptor;

        @BeforeEach
        void setUp() throws SQLException {
            mockUser = new User("john", "pass", "John",passwordUtil.generateSalt());
            mockAccount = new Account(new BigDecimal("100.00"), "Savings", "john","0123456789");
            mockUser.addAccount(mockAccount);

            lenient().when(mockService.getCurrentUser()).thenReturn(mockUser);
            lenient().when(mockService.isLoggedIn()).thenReturn(true);

            lenient().when(mockFindAccount.findUserAccount(mockAccount.getAccountNumber())).thenReturn(mockAccount);
            lenient().when(mockOwnershipValidation.isOwner(mockAccount.getAccountNumber())).thenReturn(true);

            lenient().when(mockUserepo.reloadUser(mockUser.getUsername())).thenReturn(mockUser);

            // to debug if setCurrentUser is called and what argument is passed
            lenient().doAnswer(invocation -> {
                IUser argUser = invocation.getArgument(0);
                System.out.println("setCurrentUser called with: " + argUser);
                return null;
            }).when(mockService).setCurrentUser(any());
        }


        @Test
        public void testCreateAccount_shouldAddAccountAndReloadUser() throws SQLException {
            BigDecimal initialBalance = new BigDecimal("500");
            String type = "checking";

            IAccount account = accountService.createAccount(initialBalance,type,mockUser);

            assertNotNull(account);
            verify(mockAccountDao).addAccount(account);
            verify(mockService).setCurrentUser(mockUser);
        }

        @Test
        public void testCreateAccount_shouldShowExceptionWhenNotLoggedIn(){
            when(mockService.getCurrentUser()).thenReturn(null);

            assertThrows(UserNotLoggedInException.class,()->{
                accountService.createAccount(new BigDecimal("100.00"),"savings",mockUser);
            });
        }

        //============================Deposit Test=====================
                @DisplayName("Deposits")
                @Nested
            class Deposits {
                @DisplayName("Deposit: Valid amount should increase balance and create transaction")
                @Test
                void testDepositShouldUpdateBalance() throws SQLException {
                    // Arrange
                    BigDecimal depositAmount = new BigDecimal("50.00");

                    // Act
                    accountService.deposit(mockAccount.getAccountNumber(), depositAmount);
                    verify(mockService, atLeastOnce()).getCurrentUser();
                    verify((mockService), times(1)).setCurrentUser(any());

                    // Assert
                    verify(mockService).setCurrentUser(any());
                    verify(mockService).setCurrentUser(iuserArgumentCaptor.capture());
                    IUser updateUser = iuserArgumentCaptor.getValue();

                    assertNotNull(updateUser);
                    assertEquals(new BigDecimal("150.00"), updateUser.getAccounts().get(0).getBalance());

                    ITransactions transactions = mockAccount.getTransactions().get(0);

                    assertNotNull(transactions);
                    assertEquals(mockAccount.getAccountNumber(), transactions.getAccountNumber());
                    assertEquals(depositAmount, transactions.getAmount());
                    assertEquals(TransactionType.DEPOSIT,transactions.getType());
                }

                @DisplayName("Deposit: invalid amount should throw an exception")
                @Test
                void testDepositNegativeAmount() {
                    BigDecimal negDeposit = new BigDecimal("-100");

                    assertThrows(InvalidTransactionAmount.class, () -> {
                        accountService.deposit(mockAccount.getAccountNumber(), negDeposit);
                    });

                }

                @DisplayName("Deposit: 0 amount being deposited should throw an exception")
                @Test
                void testDepositZeroAmount() {
                    BigDecimal zeroDeposit = new BigDecimal("0");

                    assertThrows(InvalidTransactionAmount.class, () -> {
                        accountService.deposit(mockAccount.getAccountNumber(), zeroDeposit);
                    });
                }

                @DisplayName("Deposit: null amount being deposited should throw an exception")
                @Test
                void testForNullDeposits() {

                    assertThrows(NullPointerException.class, () -> {
                        accountService.deposit(mockAccount.getAccountNumber(), null);
                    });
                }


                @DisplayName("Withdraw: from account that doesnt belong to the user")
                @Test
                void testDepositForAccountThatsNotTheUsers() {
                    IUser user = new User("Nate", "nate123", "Nate Dawg", passwordUtil.generateSalt());
                    IAccount acc = new Account(new BigDecimal("1000"), AccountType.SAVINGS.toString(), "Nate");
                    user.addAccount(acc);

                    assertThrows(UnauthorizedAccessException.class, () -> {
                        accountService.deposit(acc.getAccountNumber(), new BigDecimal("100"));
                    });
                }
            }

        //=====================Withdraw Test=====================
                @DisplayName("Withdraws")
              @Nested
            class Withdraws {
            @DisplayName("Withdraw : Valid amount should decrease balance and create transaction")
            @Test
            void testWithdrawShouldUpdateBalance() throws SQLException {
                BigDecimal withdraw = new BigDecimal("10");

                //Act
                accountService.withdraw(mockAccount.getAccountNumber(), withdraw);
                verify(mockService, atLeastOnce()).getCurrentUser();
                verify(mockService, times(1)).setCurrentUser(any());

                //Assert
                verify(mockService).setCurrentUser(any());
                verify(mockService).setCurrentUser(iuserArgumentCaptor.capture());
                IUser updatedUser = iuserArgumentCaptor.getValue();

                assertNotNull(updatedUser);
                assertEquals(new BigDecimal("90.00"), updatedUser.getAccounts().get(0).getBalance());

                //Transactions
                ITransactions transactions = mockAccount.getTransactions().get(0);

                assertNotNull(transactions);
                assertEquals(mockAccount.getAccountNumber(), transactions.getAccountNumber());
                assertEquals(withdraw, transactions.getAmount());
                assertEquals(TransactionType.WITHDRAW,transactions.getType());
            }


            @DisplayName("Withdraw : invalid amount should throw an exception")
            @Test
            void testNegativeWithdrawal() {
                BigDecimal negWithdrawal = new BigDecimal("-10");

                assertThrows(InvalidTransactionAmount.class, () -> {
                    accountService.withdraw(mockAccount.getAccountNumber(), negWithdrawal);
                });
            }


            @DisplayName("Withdraw : amount more than balance should throw an exception")
            @Test
            void testInsufficientAmountForWithdrawal() {
                BigDecimal insufWithdraw = new BigDecimal("1000");

                assertThrows(InsufficientFunds.class, () -> {
                    accountService.withdraw(mockAccount.getAccountNumber(), insufWithdraw);
                });
            }

            @DisplayName("Withdraw : 0 amount should throw an exception")
            @Test
            void testZeroWithdrawAmount() {
                BigDecimal zeroWithdrawal = new BigDecimal("0");

                assertThrows(InvalidTransactionAmount.class, () -> {
                    accountService.withdraw(mockAccount.getAccountNumber(), zeroWithdrawal);
                });
            }

            @DisplayName("Withdraw: null amount being withdraw should throw an exception")
            @Test
            void testForNullWithdrawal() {

                assertThrows(NullPointerException.class, () -> {
                    accountService.withdraw(mockAccount.getAccountNumber(), null);
                });
            }


            @DisplayName("Withdraw: from account that doesnt belong to the user")
            @Test
            void testWithdrawForAccountThatsNotTheUsers() {
                IUser user = new User("Nate", "nate123", "Nate Dawg", passwordUtil.generateSalt());
                IAccount acc = new Account(new BigDecimal("1000"), AccountType.SAVINGS.toString(), "Nate");
                user.addAccount(acc);

                assertThrows(UnauthorizedAccessException.class, () -> {
                    accountService.withdraw(acc.getAccountNumber(), new BigDecimal("100"));
                });
            }
        }

    //=========================Deposit and withdrawal================
    @DisplayName("Sequential Transactions")
        @Test
        void testSequentialTransactions() throws SQLException {
            //Performing deposit and withdraw
            accountService.deposit(mockAccount.getAccountNumber(),new BigDecimal("100.00"));
            accountService.withdraw(mockAccount.getAccountNumber(),new BigDecimal("50.00"));

            //act & assert - verifying getCurrentUser was called twice
            verify(mockService,atLeastOnce()).getCurrentUser();

            //Capture both calls to setCurrentUser
            verify(mockService, times(2)).setCurrentUser(iuserArgumentCaptor.capture());

            //get the final capture User
            List<IUser> allCapturedUsers = iuserArgumentCaptor.getAllValues();
            assertEquals(2,allCapturedUsers.size());

            IUser finalUser = allCapturedUsers.get(1);
            assertNotNull(finalUser);
            assertEquals(new BigDecimal("150.00"), finalUser.getAccounts().get(0).getBalance());

            List<ITransactions> transactions = finalUser.getAccounts().get(0).getTransactions();
            assertNotNull(transactions);
            //For deposit
            assertEquals(new BigDecimal("100.00"),transactions.get(0).getAmount());
            assertEquals(TransactionType.DEPOSIT, transactions.get(0).getType());

            //For withdraw
            assertEquals(new BigDecimal("50.00"),transactions.get(1).getAmount());
            assertEquals(TransactionType.WITHDRAW, transactions.get(1).getType());



    }

        //=========================Transfer Between Accounts================
        @DisplayName("Transfer between Accounts")
    @Test
        public void testTransferBetweenAccounts() throws SQLException {
            IAccount account = new Account(new BigDecimal("100"),"savings",mockUser.getUsername(),"9872563725");
            BigDecimal amount = new BigDecimal("100");
            when(mockFindAccount.findUserAccount(account.getAccountNumber())).thenReturn(account);



            when(mockAccountDao.updateAccountBalanceTransfer(
                    mockAccount.getAccountNumber(),
                    account.getAccountNumber(),
                    new BigDecimal("100")

            )).thenReturn(true);

            boolean check = accountService.transfer(mockAccount.getAccountNumber(),account.getAccountNumber(),new BigDecimal("100"));

            assertTrue(check,"Transfer should return true");
            verify(mockFindAccount).findUserAccount(mockAccount.getAccountNumber());
            verify(mockFindAccount).findUserAccount(account.getAccountNumber());
            verify(mockAccountDao).updateAccountBalanceTransfer(mockAccount.getAccountNumber(),account.getAccountNumber(),amount);
    }


    @Test
        public void testTransferBetweenAccounts_SameAccountShouldThrowException(){

            assertThrows(TransferToSameAccountException.class, ()->{
                accountService.transfer(mockAccount.getAccountNumber(),mockAccount.getAccountNumber(),new BigDecimal("50.00"));
            });
    }

    @Test
        public void testTransferBetweenAccounts_InsufficientBalance() throws SQLException {
            IAccount account = new Account(new BigDecimal("50.00"),"checking",mockUser.getUsername());

            when(mockFindAccount.findUserAccount(account.getAccountNumber())).thenReturn(account);
            when(mockOwnershipValidation.isOwner(account.getAccountNumber())).thenReturn(true);

            assertThrows(InvalidTransferAmountFrom.class, ()->{
               accountService.transfer(account.getAccountNumber(),mockAccount.getAccountNumber(),new BigDecimal("1000.00"));
            });
    }

    @Test
        public void testTransferBetweenAccounts_NegativeAmount() throws SQLException {
        IAccount testAccount = new Account(new BigDecimal("50.00"),"checking",mockUser.getUsername());

        when(mockFindAccount.findUserAccount(testAccount.getAccountNumber())).thenReturn(testAccount);
        when(mockOwnershipValidation.isOwner(testAccount.getAccountNumber())).thenReturn(true);

        assertThrows(InvalidTransactionAmount.class, ()->{
            accountService.transfer(testAccount.getAccountNumber(),mockAccount.getAccountNumber(),new BigDecimal("-1000.00"));
        });
    }


        //=========================Deleting Accounts================
        @DisplayName("Deleting Accounts")

        @Test
        public void testDeletingAccountSuccessfully() throws SQLException {
            IAccount testAccount = new Account(new BigDecimal("0"),"savings",mockUser.getUsername());

            when(mockFindAccount.findUserAccount(testAccount.getAccountNumber())).thenReturn(testAccount);

            boolean check = accountService.deleteAccount(testAccount.getAccountNumber());

            assertTrue(check);
        }

        @Test
        public void testDeletingAccountUnsuccessfully_AccountWithFundsShouldThrowException() throws SQLException {
            IAccount testAccount = new Account(new BigDecimal("100"),"savings",mockUser.getUsername());

            when(mockFindAccount.findUserAccount(testAccount.getAccountNumber())).thenReturn(testAccount);

            assertThrows(DeletingWithFundsException.class,()->{
                accountService.deleteAccount(testAccount.getAccountNumber());
            });
        }


        @Test
        public void testDeletingAccountUnsuccessfully_UnauthorizedShouldThrowException() throws SQLException {
            IAccount testAccount = new Account(new BigDecimal("0"),"savings","Nathan");

            when(mockFindAccount.findUserAccount(testAccount.getAccountNumber())).thenReturn(testAccount);

            assertThrows(UnauthorizedAccessException.class,()->{
                accountService.deleteAccount(testAccount.getAccountNumber());
            });
        }

        @Test
        public void testGetAccountDetails() throws SQLException {

            when(mockFindAccount.findUserAccount(mockAccount.getAccountNumber())).thenReturn(mockAccount);

            IAccount account = accountService.getAccountDetails(mockAccount.getAccountNumber());

            assertNotNull(account);
            assertEquals(new BigDecimal("100.00"),account.getBalance(),"Balance should be 100");
            assertEquals(AccountType.getEnum("savings"),account.getType());
        }

        @Test
        public void testGetAccountDetails_UnauthorizedShouldThrowException() throws SQLException {
            IAccount testAccount = new Account(new BigDecimal("0"),"savings","Nathan");
            when(mockOwnershipValidation.isOwner(testAccount.getAccountNumber())).thenReturn(false);
            assertThrows(UnauthorizedAccessException.class,()->{
                accountService.getAccountDetails(testAccount.getAccountNumber());
            });
        }
}


