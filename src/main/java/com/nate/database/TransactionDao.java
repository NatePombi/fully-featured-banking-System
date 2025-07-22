package com.nate.database;

import com.nate.model.ITransactions;
import com.nate.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements ITransactionDao{


    @Override
    public void saveTransaction(Connection newCon ,ITransactions tx ) throws SQLException {
        String save = "INSERT INTO transactions(account_number,to_account,description,amount,type,timestamp) VALUES(?,?,?,?,?,?)";
        try(PreparedStatement stm = newCon.prepareStatement(save)){
            newCon.setAutoCommit(false);
            stm.setString(1,tx.getAccountNumber());
            stm.setString(2, tx.getToAccount());
            stm.setString(3,tx.getDescription());
            stm.setBigDecimal(4, tx.getAmount());
            stm.setString(5,tx.getType().toString());
            stm.setTimestamp(6,Timestamp.valueOf(tx.getTimeStamp()));
            int row =stm.executeUpdate();

            if(row>0){
                newCon.commit();
            }
            else{
                newCon.rollback();
            }
        }

        catch (SQLException e){
            newCon.rollback();
            throw e;
        }

    }


    @Override
    public List<ITransactions> getTransactionsByAccountNumber(String accountNumber) throws SQLException {
        String query = "SELECT * FROM transactions WHERE account_number = ?";
        List<ITransactions> transactions = new ArrayList<>();
        try(Connection con = DBFunction.getConnection();
            PreparedStatement stm = con.prepareStatement(query);

        ){
            stm.setString(1,accountNumber);

            try( ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    String desc = rs.getString("description");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    String toAccount = rs.getString("to_account");
                    Timestamp timestamp = rs.getTimestamp("timestamp");

                    transactions.add(new Transaction(type, amount, desc, accountNumber,timestamp.toLocalDateTime()));



                }
            }
        }


        return transactions;
    }
}
