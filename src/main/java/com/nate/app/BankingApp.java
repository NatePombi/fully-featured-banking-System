package com.nate.app;

import com.nate.database.*;
import com.nate.ui.Start;
import com.nate.util.*;

import java.sql.SQLException;

public class BankingApp {
    public static void main(String[] args) {
       ExceptionHandler exceptionHandler = new ExceptionHandler();
        try {
            //Creating Db tables upon startup
            DBFunction.databaseInit();
            //starts up the app
            Start start = Bootstrap.init();

            run(start);
        } catch (SQLException | InterruptedException e) {
            exceptionHandler.handleException(e);
        }
    }

    public static void run(Start start) throws SQLException, InterruptedException {
        start.start();
    }

}
