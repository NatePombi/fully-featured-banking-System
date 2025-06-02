package database;

import model.ITransactions;

import java.sql.*;
import java.util.List;

public interface ITransactionDao {
     void saveTransaction(ITransactions tx ) throws SQLException;


     List<ITransactions> getTransactionsByAccountNumber(String accountNumber);
}
