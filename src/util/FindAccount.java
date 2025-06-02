package util;

import database.AccountDao;
import database.IAccountDao;
import database.ITransactionDao;
import database.TransactionDao;
import exception.AccountNotFoundException;
import model.IAccount;

import java.sql.SQLException;

public class FindAccount {
    private final ITransactionDao transactionDao;
    private final IAccountDao accountDao;

    public FindAccount(){
        this.transactionDao = new TransactionDao();
        this.accountDao = new AccountDao(transactionDao);
    }
    //finding the account with the specified account number in repository
    public IAccount findUserAccount(String accountNumber) throws SQLException {

        return accountDao.getAll().stream()
                .filter(acc-> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(()->new AccountNotFoundException(accountNumber));
    }
}
