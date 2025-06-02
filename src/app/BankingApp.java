package app;

import database.*;
import repository.Config;
import service.*;
import ui.MenuRenderer;
import ui.Start;
import util.*;

import java.sql.SQLException;

public class BankingApp {
    public static void main(String[] args) {
       ExceptionHandler exceptionHandler = new ExceptionHandler();
        try {
            //disables test mode upon start up
            Config.TEST_MODE = false;
            //Creating Db tables upon startup
            database.dbFunction.databaseInit();
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
