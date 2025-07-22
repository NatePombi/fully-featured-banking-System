package Test.service;

import com.nate.database.UserDao;
import com.nate.model.IUser;
import com.nate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nate.service.SessionService;
import com.nate.service.UserService;
import com.nate.util.PasswordUtil;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private SessionService mockSessionService;
    @Mock
    private UserDao mockUserDao;
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

    @DisplayName("Register Test: registering with a null/blank username")
    @Test
    void testRegisterUserFailsWhenUsernameIsNullOrBlank(){

            assertThrows(NullPointerException.class,()->{
                userService.registerUser("","test","tester");
            });


            assertThrows(NullPointerException.class,()->{
                userService.registerUser(null,"test","tester");
            });

    }

    @DisplayName("Register Test: registering with a null/blank password")
    @Test
    void testRegisterUserFailsWhenPasswordNullOrBlank(){

        assertThrows(NullPointerException.class,()->{
            userService.registerUser("test1","","tester");
        });


        assertThrows(NullPointerException.class,()->{
            userService.registerUser("test1",null,"tester");
        });


    }


    @DisplayName("Register Test: registering with a null/blank name")
    @Test
    void testRegisterUserFailsWhenNameNullOrBlank(){

        assertThrows(NullPointerException.class,()->{
            userService.registerUser("test1","test","");
        });


        assertThrows(NullPointerException.class,()->{
            userService.registerUser("test1","test",null);
        });


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

            assertThrows(IllegalArgumentException.class, ()->{
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


        @DisplayName("Login Test: logging in with a null/ empty username")
        @Test
        void testLoginUnsuccessfulWithNullOrBlankUsername() throws SQLException {
            String name = "Test One";
            String username = "test";
            String password = "test123";
            String salt = passwordUtil.generateSalt();
            String hashPassword = passwordUtil.hash(password, salt);


            IUser user = new User(username, hashPassword, name, salt);

            assertThrows(NullPointerException.class,()->{
               userService.loginUser("",user.getPassword());
            });

            assertThrows(NullPointerException.class,()->{
                userService.loginUser(null,user.getPassword());
            });
        }

        @DisplayName("Login Test: logging in with a null/ empty password")
        @Test
        void testLoginUnsuccessfulWithNullOrBlankPassword() throws SQLException {
            String name = "Test One";
            String username = "test";
            String password = "test123";
            String salt = passwordUtil.generateSalt();
            String hashPassword = passwordUtil.hash(password, salt);


            IUser user = new User(username, hashPassword, name, salt);

            assertThrows(NullPointerException.class,()->{
                userService.loginUser(username,"");
            });

            assertThrows(NullPointerException.class,()->{
                userService.loginUser(username,null);
            });
        }
    }

    @DisplayName("Get User by Username Test")
    @Nested
    class getUserByUsername{
        @DisplayName("Get User Test: getting user by username succesfully")
        @Test
        void testGetUserByUsernameSuccessfully() throws SQLException {
            String name = "Test One";
            String username = "test";
            String password = "test123";
            String salt = passwordUtil.generateSalt();
            String hashPassword = passwordUtil.hash(password, salt);


            IUser user = new User(username, hashPassword, name, salt);

            when(mockUserDao.findUserByUsername(user.getUsername())).thenReturn(user);

            IUser user1 = userService.getUserByUsername(user.getUsername());

            assertNotNull(user1);
            assertEquals(username,user1.getUsername(),"Username should be test");
            assertEquals(name, user1.getName(),"Name should be Test One");
        }

        @DisplayName("Get User Test: getting user by username unsuccessfully")
        @Test
        void testGetUserByUsernameUnsuccessfully_ShouldThrowAnException(){

            assertThrows(Exception.class,()->{
                userService.getUserByUsername("hbfs");
            });
        }
    }

}
