package com.nate.database;

import com.nate.model.ITransactions;

import java.sql.*;
import java.util.List;

public interface ITransactionDao {
     void saveTransaction(Connection con ,ITransactions tx ) throws SQLException;


     List<ITransactions> getTransactionsByAccountNumber(String accountNumber) throws SQLException;
}
