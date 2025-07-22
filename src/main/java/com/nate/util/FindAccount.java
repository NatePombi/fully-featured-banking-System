package com.nate.util;

import com.nate.database.AccountDao;
import com.nate.database.IAccountDao;
import com.nate.database.ITransactionDao;
import com.nate.database.TransactionDao;
import com.nate.exception.AccountNotFoundException;
import com.nate.model.IAccount;
import com.nate.model.ITransactions;

import java.sql.SQLException;

public class FindAccount {
    private final ITransactionDao transactionDao;
    private final IAccountDao accountDao;

    public FindAccount(ITransactionDao transactionDao, IAccountDao accountDao){
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }
    //finding the account with the specified account number in repository
    public IAccount findUserAccount(String accountNumber) throws SQLException {

        return accountDao.getAll().stream()
                .filter(acc-> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(()->new AccountNotFoundException(accountNumber));
    }
}
