package model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user in the banking system.
 * Each user has a username, name, hashed password with salt, and a list of accounts.
 */
public class User implements IUser{
    private final String username;           // Unique identifier for the user
    private final String passwordHash;            // password thats taken to be hashed
    private final String salt;                    // Salt used for hashing the password
    private final String name;               // Full name of the user
    private final List<IAccount> accounts;    // List of accounts owned by the user


    /**
     * Constructs a new User with the given credentials and name.
     * Password is hashed securely with a generated salt.
     */

    public User(String username, String passwordHash, String name, String salt) {
        this.username = username;
        this.salt = salt;                  // Generate a new salt
        this.passwordHash = passwordHash; // Hash the password with salt
        this.name = name;
        this.accounts = new ArrayList<>();
    }







    /**
     * removes account with the associated account number
     */
    @Override
    public void removeAccount(String accountNumber){
        accounts.removeIf(acc ->acc.getAccountNumber().equals(accountNumber));
    }
    //Getters for every field

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSalt() {
        return salt;
    }


    /**
     * Adds an account into the accounts list
     */
    @Override
    public void addAccount(IAccount account){
        accounts.add(account);
    }

    /**
     * Returns an unmodifiable list of accounts to prevent external modification.
     */
    @Override
    public List<IAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Users are considered equal if their usernames are equal.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    /**
     * Returns a formatted string of the user including name, username, and accounts.
     */
    @Override
    public String toString() {
        return String.format("""
                User: %s
                Username: %s
                Accounts:\s
                %s
               \s""", name,username,accounts);
    }
}
