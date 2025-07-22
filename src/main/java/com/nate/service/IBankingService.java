package com.nate.service;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface IBankingService {

     void openAccount(BigDecimal amount, String type);


   boolean viewBalance(String accountNumber) throws SQLException ;
    }



