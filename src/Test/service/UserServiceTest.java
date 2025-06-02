package Test.service;

import model.IUser;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.SessionService;
import service.UserService;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private SessionService mockSessionService;
    @Mock
    private database.UserDao mockUserDao;
    @InjectMocks
    private UserService userService;

    private IUser testUser;
    private PasswordUtil passwordUtil;

    @BeforeEach
     void startup(){
        this.passwordUtil = new PasswordUtil();
    }

@Nested
class Register {
        @DisplayName("Register Tests: registering successfully no exceptions thrown")
    @Test
    void testRegisterUserSuccessfully() throws SQLException {
        String name = "Test One";
        String username = "test";
        String password = "test123";

        when(mockUserDao.findUserByUsername("test")).thenReturn(null); //simulates user doesn't exist

        boolean register = userService.registerUser(username, password, name);
        assertTrue(register);

        verify(mockUserDao).addUser(any(User.class));
    }

    @DisplayName("Register Tests: registering with an existing username should throw an exception")
    @Test
    void testRegisterUserFailsWhenExists() throws SQLException {
        IUser existingUser = mock(IUser.class);
        when(mockUserDao.findUserByUsername("test")).thenReturn(existingUser); //simulates existing user

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("test", "test123", "Test one");
        });

        verify(mockUserDao, never()).addUser(any());
    }
}
    @DisplayName("Login Tests")
    @Nested
    class Login {
        @DisplayName("Login TestS: Login successfully with correct credentials")
        @Test
        void testLoginUserSuccessfully() throws SQLException {
            String name = "Test One";
            String username = "test";
            String password = "test123";
            String salt = passwordUtil.generateSalt();
            String hashPassword = passwordUtil.hash(password, salt);


            IUser user = new User(username, hashPassword, name, salt);

            when(mockUserDao.findUserByUsername("test")).thenReturn(user);


            boolean login = userService.loginUser(username, password);
            assertTrue(login);

            assertEquals(username, user.getUsername());
            verify(mockUserDao).findUserByUsername("test");
        }

        @DisplayName("Login Tests: testing for no user present with that username")
        @Test
        void testLoginUnsuccessfulWithNonExistentUsername(){

            assertThrows(NoSuchElementException.class, ()->{
                userService.loginUser("test","1234");
            });
        }

        @DisplayName("Login Tests: testing for user present with that username but password incorrect")
        @Test
        void testLoginUnsuccessfulWithWrongPassword() throws SQLException {
            String name = "Test One";
            String username = "test";
            String password = "test123";
            String salt = passwordUtil.generateSalt();
            String hashPassword = passwordUtil.hash(password, salt);


            IUser user = new User(username, hashPassword, name, salt);

            when(mockUserDao.findUserByUsername("test")).thenReturn(user);


            assertThrows(IllegalArgumentException.class, ()->{
                userService.loginUser("test","1234");
            });
        }
    }


}
