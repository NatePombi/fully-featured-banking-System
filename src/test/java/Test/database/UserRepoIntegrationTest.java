package Test.database;

import com.nate.database.Configuration;
import com.nate.database.DBFunction;
import com.nate.database.UserDao;
import com.nate.database.UserRepo;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.util.PasswordUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepoIntegrationTest {

    private UserRepo userRepo;
    private PasswordUtil passwordUtil;
    private IUser mockUser;
    private UserDao userDao;
    @BeforeEach
    void startUp() throws SQLException {
        Configuration.config = true;
        DBFunction.databaseInit();
        passwordUtil = new PasswordUtil();
        String salt = passwordUtil.generateSalt();
        String hash = passwordUtil.hash("test123",salt);
        mockUser = new User("Tester",hash,"tester",salt);
        userDao = new UserDao();
        userRepo = new UserRepo();

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

    @DisplayName("Reload User Test")
    @Test
    void testReloadUser() throws SQLException {
        userDao.addUser(mockUser);
        IUser user =userRepo.reloadUser(mockUser.getUsername());
        assertNotNull(user);

        assertEquals("tester",user.getName());
    }
}
