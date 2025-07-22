package Test.database;

import com.nate.database.Configuration;
import com.nate.database.DBFunction;
import com.nate.database.UserDao;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoIntegrationTest {

    private UserDao userDao;
    private IUser mockUser;
    private PasswordUtil passwordUtil;
    @BeforeEach
    void startUp() throws SQLException {
        Configuration.config = true;
        DBFunction.databaseInit();
        passwordUtil = new PasswordUtil();
        String salt = passwordUtil.generateSalt();
        String hash = passwordUtil.hash("tester",salt);
        mockUser = new User("testing",hash,"Test1",salt);
        userDao = new UserDao();
    }

    @AfterEach
    void reset() throws SQLException {

        try(Connection con = DBFunction.getConnection();
            Statement stm = con.createStatement()
        ){
            stm.execute("DELETE FROM transactions");
            stm.execute("DELETE FROM users");
            stm.execute("DELETE FROM accounts");
        }
    }

    @DisplayName("Add User Test")
    @Test
    void testAddUser() throws SQLException {

        assertDoesNotThrow(()->{
            userDao.addUser(mockUser);
        });

        IUser user = userDao.findUserByUsername(mockUser.getUsername());

        assertEquals("testing",user.getUsername(),"Username should be testing");
    }


    @DisplayName("Deleting User Test")
    @Test
    void testDeleteUser() throws SQLException {
        userDao.addUser(mockUser);

        assertDoesNotThrow(()->{
            userDao.deleteUser(mockUser.getUsername());
        });

        IUser user = userDao.findUserByUsername(mockUser.getUsername());
        assertNull(user);

    }

}
