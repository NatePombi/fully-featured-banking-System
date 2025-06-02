package database;

import exception.*;
import model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountDao implements IAccountDao{
private ITransactionDao transactionDao;

    public AccountDao(ITransactionDao transactionDao){
        this.transactionDao = transactionDao;
}
    // Add a new account
    @Override
    public void addAccount(IAccount account) throws SQLException {
        String sql = "INSERT INTO accounts (owner_name, balance, type,id) VALUES (?, ?, ?,?)";
        try (Connection conn = dbFunction.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getOwnerUsername());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setString(3, account.getType().toString());
            pstmt.setString(4,account.getAccountNumber());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<IAccount> getAll() throws SQLException {
        List<IAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = dbFunction.getConnection();
             Statement pstmt = conn.createStatement()) {
            ResultSet rs = pstmt.executeQuery(sql);
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
    public void updateAccountBalanceTransfer(String fromAccountNumber,String toAccountNumber, BigDecimal newFrom, BigDecimal newTo, BigDecimal amount) throws SQLException {
        try (Connection con = dbFunction.getConnection()) {
            con.setAutoCommit(false);

            try(PreparedStatement stm1 = con.prepareStatement("UPDATE accounts  SET balance = ? WHERE id = ?");
                PreparedStatement stm2 = con.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?");
            ){
                stm1.setBigDecimal(1, newFrom);
                stm1.setString(2,fromAccountNumber);
                stm1.executeUpdate();

                stm2.setBigDecimal(1,newTo);
                stm2.setString(2, toAccountNumber);
                stm2.executeUpdate();


                 ITransactions transferTransactionFrom = new Transaction(TransactionType.TRANSFER.name(),amount,"Transfer Withdrawal",fromAccountNumber);
                 ITransactions transferTransactionTo = new Transaction(TransactionType.DEPOSIT.name(),amount,"Transfer Deposit",toAccountNumber );
                transactionDao.saveTransaction(transferTransactionFrom);
                transactionDao.saveTransaction(transferTransactionTo);
                con.commit();
            }

        }

    }


    // Update account balance
    @Override
    public void updateAccountBalanceDeposit(String accountNumber, BigDecimal amount, IUser user) throws SQLException {

        try(Connection con = dbFunction.getConnection()){

            String userCheck = "SELECT * FROM accounts WHERE id = ? AND owner_name = ?";
            try(PreparedStatement stm = con.prepareStatement(userCheck)){
                stm.setString(1,accountNumber);
                stm.setString(2,user.getUsername());
                try(ResultSet rs = stm.executeQuery()){
                    if(!rs.next()){
                        throw new UnauthorizedAccessException(accountNumber);
                    }
                }
            }

            String deposit = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            try(PreparedStatement stm = con.prepareStatement(deposit)){
                stm.setBigDecimal(1,amount);
                stm.setString(2,accountNumber);
                stm.executeUpdate();
            }
        }
        ITransactions transactions = new Transaction("deposit",amount,"Deposit",accountNumber);
        transactionDao.saveTransaction(transactions);
    }

    @Override
    public void updateAccountBalanceWithdraw(String accountNumber, BigDecimal amount, IUser user) throws SQLException {

        try(Connection con = dbFunction.getConnection()){

            String userCheck = "SELECT 1 FROM accounts WHERE id = ? AND owner_name = ?";
            try(PreparedStatement stm = con.prepareStatement(userCheck)){
                stm.setString(1,accountNumber);
                stm.setString(2,user.getUsername());
                try(ResultSet rs = stm.executeQuery()){
                    if(!rs.next()){
                        throw new UnauthorizedAccessException(accountNumber);
                    }
                }
            }

            String deposit = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            try(PreparedStatement stm = con.prepareStatement(deposit)){
                stm.setBigDecimal(1,amount);
                stm.setString(2,accountNumber);
                stm.executeUpdate();
            }
        }
        ITransactions transactions = new Transaction("withdraw",amount,"withdrawal",accountNumber);
        transactionDao.saveTransaction(transactions);
    }


    @Override
    public List<IAccount> getAllAccountsByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE owner_name = ?";
        List<IAccount> accounts = new ArrayList<>();
        try (Connection conn = dbFunction.getConnection();
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
        String sql = "SELECT 1 FROM accounts WHERE id = ?";
        IAccount accounts = null;
        try (Connection conn = dbFunction.getConnection();
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
        try(Connection con = dbFunction.getConnection();
            PreparedStatement stm = con.prepareStatement(delete);
        ){
            stm.setString(1,accountNumber);
            stm.executeUpdate();

        }
    }

}
