package util;

import Test.service.PasswordUtilTest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
/**
 * Utility class to manage password hashing, salting, and verification.
 */
public class PasswordUtil {

    public PasswordUtil(){

    }

    /**
     * Generates a random 16-byte salt and encodes it in Base64.
     * @return a Base64-encoded random salt
     */
    public String generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }


    /**
     * Hashes the password with the given salt using SHA-256.
     * @param password the user's password
     * @param salt the salt to mix with the password
     * @return the resulting hash in hexadecimal format
     */
    public String hash(String password,String salt){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salted = (salt + password).getBytes(StandardCharsets.UTF_8);
            byte[] hashByte = digest.digest(salted);
            return byteToHex(hashByte);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported");
        }
    }


    /**
     * Verifies if the input password (after hashing with stored salt) matches the stored hash.
     * @param inputPassword user input
     * @param storedHash the correct password hash
     * @param storedSalt the original salt used when the password was stored
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String inputPassword, String storedHash, String storedSalt){
        String hashInput = hash(inputPassword, storedSalt);
        return hashInput.equals(storedHash);
    }


    /**
     * Converts a byte array to a hexadecimal string.
     * @param bytes the byte array to convert
     * @return hexadecimal string
     */
    private String byteToHex(byte[] bytes){

        StringBuilder builder = new StringBuilder();
        for(byte b : bytes){
            builder.append(String.format("%02x",b));
        }
        return builder.toString();
    }
}
