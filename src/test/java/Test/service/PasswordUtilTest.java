package Test.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.nate.util.PasswordUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PasswordUtilTest {
    private PasswordUtil passwordUtil = new PasswordUtil();

    @DisplayName("Password Hashing")
    @Nested
    class passwordHashing {
        @Test
        void testPasswordHashingConsistency () {

        String password = "password123";
        String salt = passwordUtil.generateSalt();
        String hash1 = passwordUtil.hash(password, salt);
        String hash2 = passwordUtil.hash(password, salt);
        assertEquals(hash1, hash2);
    }

        @Test
        void testDifferentPasswordsProduceDifferentHashes () {
        String salt = passwordUtil.generateSalt();
        String hash1 = passwordUtil.hash("Man123", salt);
        String hash2 = passwordUtil.hash("test123", salt);
        assertNotEquals(hash1, hash2);
    }

        @Test
        void testSaltRandomness () {
        String salt = passwordUtil.generateSalt();
        String salt2 = passwordUtil.generateSalt();

        assertNotEquals(salt, salt2);
    }
    }
}
