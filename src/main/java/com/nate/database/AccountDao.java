package com.nate.database;

import com.nate.exception.*;
import com.nate.model.*;
import com.nate.util.NegOrZeroCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Account repository handles all the CRUD operations for the account
 * implements the IAccountDao interface
 */
public class AccountDao implements IAccountDao{
    private final Logger log = LoggerFactory.getLogger(AccountDao.class);
private ITransactionDao transactionDao;


    public AccountDao(ITransactionDao transactionDao){
        this.transactionDao = transactionDao;
}

    /**
     * Adds a specified account to the database
     * <p>
     * This method uses a prepared statement to insert the account details
     * if the insert fails, it rolls back the transaction and logs a warning
     * Any SQl related exceptions are logged and rethrown
     *
     * @param account account that will be added to the database
     * @throws SQLException if any database access errors occur
     * @throws IllegalArgumentException if the insert operation fails (e.g no rows effected)
     */
    @Override
    public void addAccount(IAccount account) throws SQLException {
        String sql = "INSERT INTO accounts (owner_name, balance, type,id) VALUES (?, ?, ?,?)";
        try (Connection conn = DBFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            pstmt.setString(1, account.getOwnerUsername());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setString(3, account.getType().toString());
            pstmt.setString(4,account.getAccountNumber());

            int row = pstmt.executeUpdate();
            if(row>0){
                conn.commit();
            }else{
                conn.rollback();
                log.warn("Failed to add Account");
                throw new IllegalArgumentException("Failed to add account");
            }
        }catch (SQLException e){
            log.error("SQL Error",e);
            throw e;
        }
    }

    /**
     * Retrieves all accounts from the database
     * @return a list of Accounts from the database
     * @throws SQLException if any database access errors occur
     */
    @Override
    public List<IAccount> getAll() throws SQLException {
        List<IAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DBFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery())
        {

            while (rs.next()) {
                BigDecimal balance = rs.getBigDecimal("balance");
                String accountType = rs.getString("type");
                String name = rs.getString("owner_name");
                String accNum = rs.getString("id");
                accounts.add(new Account(balance, accountType, name,accNum));
            }
            return accounts;
        }
    }

    // Update account balance
    @Override
    public boolean updateAccountBalanceTransfer(String fromAccountNumber,String toAccountNumber, BigDecimal amount) throws SQLException {

        String getFrom = "SELECT * FROM accounts WHERE id = ?";
        String getTo = "SELECT * FROM accounts WHERE id = ?";

        IAccount acc1;
        IAccount acc2;

        BigDecimal newFrom ;
        BigDecimal newTo ;

        try (Connection con = DBFunction.getConnection();
             PreparedStatement pstm1 = con.prepareStatement(getFrom);
             PreparedStatement pstm2 = con.prepareStatement(getTo)
        ) {
            con.setAutoCommit(false);
            pstm1.setString(1,fromAccountNumber);
            pstm2.setString(1,toAccountNumber);

            ResultSet rs = pstm1.executeQuery();
            acc1 = getAccountFromResultSet(rs,fromAccountNumber);
            if(NegOrZeroCheck.isNegative(acc1.getBalance(),amount)) {
                throw new InsufficientFunds(acc1);
            }
            newFrom = acc1.getBalance().subtract(amount);

            rs = pstm2.executeQuery();
            acc2 = getAccountFromResultSet(rs,toAccountNumber);
                newTo = acc2.getBalance().add(amount);


            try(PreparedStatement stm1 = con.prepareStatement("UPDATE accounts  SET balance = ? WHERE id = ?");
                PreparedStatement stm2 = con.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?");
            ){


                stm1.setBigDecimal(1, newFrom);
                stm1.setString(2,fromAccountNumber);
                int row = stm1.executeUpdate();

                if(row<=0){
                    con.rollback();
                    log.warn("Failed to transfer funds");
                    throw new FailedToTransferFundsException();
                }

                stm2.setBigDecimal(1,newTo);
                stm2.setString(2, toAccountNumber);
                int row2 =stm2.executeUpdate();

                if(row2<=0){
                    con.rollback();
                    log.warn("Failed to transfer funds");
                    throw new FailedToTransferFundsException();
                }


                 ITransactions transferTransactionFrom = new Transaction(TransactionType.TRANSFER.name(),amount,"Transfer Withdrawal",fromAccountNumber);
                 ITransactions transferTransactionTo = new Transaction(TransactionType.DEPOSIT.name(),amount,"Transfer Deposit",toAccountNumber );
                transactionDao.saveTransaction(con,transferTransactionFrom);
                transactionDao.saveTransaction(con ,transferTransactionTo);
                con.commit();
                return true;
            }
            catch (SQLException e){
                log.error("SQL Error",e);
                throw e;
            }

        }

    }


    // Update account balance
    @Override
    public void updateAccountBalanceDeposit(String accountNumber, BigDecimal amount, IUser user) throws SQLException {
            Connection con = DBFunction.getConnection();
        try{
            con.setAutoCommit(false);
            String userCheck = "SELECT * FROM accounts WHERE id = ? AND owner_name = ?";
            try(PreparedStatement stm = con.prepareStatement(userCheck)){
                stm.setString(1,accountNumber);
                stm.setString(2,user.getUsername());
                try(ResultSet rs = stm.executeQuery()){
                    if(!rs.next()){
                        log.warn("You do not own this account");
                        throw new UnauthorizedAccessException(accountNumber);
                    }
                }
            }

            String deposit = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            try(PreparedStatement stm = con.prepareStatement(deposit)){
                stm.setBigDecimal(1,amount);
                stm.setString(2,accountNumber);
                int row = stm.executeUpdate();
                if(row>0){
                    con.commit();
                }else{
                    log.warn("Failed to update the balance of account with account number {}",accountNumber);
                    con.rollback();
                    throw new IllegalArgumentException("Failed to update account balance");
                }
            }
        }
        catch (SQLException e){
            log.error("SQL Error",e);
            throw e;
        }
        ITransactions transactions = new Transaction("deposit",amount,"Deposit",accountNumber);
        transactionDao.saveTransaction(con,transactions);
        con.close();
    }

    @Override
    public void updateAccountBalanceWithdraw(String accountNumber, BigDecimal amount, IUser user) throws SQLException {
            Connection con = DBFunction.getConnection();
        try{
            con.setAutoCommit(false);

            String userCheck = "SELECT 1 FROM accounts WHERE id = ? AND owner_name = ?";
            try(PreparedStatement stm = con.prepareStatement(userCheck)){
                stm.setString(1,accountNumber);
                stm.setString(2,user.getUsername());
                try(ResultSet rs = stm.executeQuery()){
                    if(!rs.next()){
                        log.warn("You do not own this account with account number {}",accountNumber);
                        throw new UnauthorizedAccessException(accountNumber);
                    }
                }
            }

            String deposit = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            try(PreparedStatement stm = con.prepareStatement(deposit)){
                stm.setBigDecimal(1,amount);
                stm.setString(2,accountNumber);
                int row = stm.executeUpdate();
                if(row>0){
                    con.commit();
                }else {
                    log.warn("Failed to update account balance");
                    con.rollback();
                    throw new IllegalArgumentException("Failed to update account balance");
                }
            }
        }catch (SQLException e){
            log.error("SQL Error",e);
            throw e;
        }
        ITransactions transactions = new Transaction("withdraw",amount,"withdrawal",accountNumber);
        transactionDao.saveTransaction(con,transactions);
        con.close();
    }


    @Override
    public List<IAccount> getAllAccountsByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE owner_name = ?";
        List<IAccount> accounts = new ArrayList<>();
        try (Connection conn = DBFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                BigDecimal balance = rs.getBigDecimal("balance");
                String type = rs.getString("type");
                String acc = rs.getString("id");
                accounts.add(new Account( balance, type,username,acc));
            }
        }


        return accounts;
    }

    @Override
    public IAccount getAccountsByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        IAccount accounts = null;
        try (Connection conn = DBFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                BigDecimal balance = rs.getBigDecimal("balance");
                String type = rs.getString("type");
                String name = rs.getString("owner_name");
                accounts = new Account( balance, type,name,accountNumber);
            }
        }
        return accounts;
    }

    @Override
    public void deleteAccountByAccountNumber(String accountNumber) throws SQLException {
        String delete = "DELETE FROM accounts WHERE id = ?";
        try(Connection con = DBFunction.getConnection();
            PreparedStatement stm = con.prepareStatement(delete);
        ){
            con.setAutoCommit(false);
            stm.setString(1,accountNumber);
            int row = stm.executeUpdate();
            if(row>0){
                con.commit();
            }else{
                log.warn("Failed to delete Account with account number{}",accountNumber);
                con.rollback();
                throw new IllegalArgumentException("Failed to delete account");
            }
        }
        catch(SQLException e){
            log.error("SQL Error",e);
            throw e;
        }
    }

    private IAccount getAccountFromResultSet(ResultSet rs, String accountNumber) throws SQLException {
        if(rs.next()){
            String owner = rs.getString("owner_name");
            String type = rs.getString("type");
            BigDecimal balance = rs.getBigDecimal("balance");

            return new Account(balance,type,owner,accountNumber);
        }

        throw new AccountNotFoundException(accountNumber);
    }

}
