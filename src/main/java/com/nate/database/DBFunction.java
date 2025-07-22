package com.nate.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/*

 */
public class DBFunction {

    private static final String PROPERTIES_FILE = "/com/database/db.properties" ;
    private static final Logger log = LoggerFactory.getLogger(DBFunction.class);
    private static final HikariDataSource dataSource;

    static {


        try(InputStream inputStream = DBFunction.class.getResourceAsStream(PROPERTIES_FILE)){
            Properties prop = new Properties();

            if(inputStream == null){
                log.warn("Cannot find properties {}",PROPERTIES_FILE);
                throw new IllegalStateException("Cannot find properties " + PROPERTIES_FILE);
            }

            prop.load(inputStream);
            HikariConfig config = new HikariConfig();
            String envUrl = null;
            if (Configuration.config) {
                envUrl = System.getenv("DB_TEST_URL");
            } else {
                envUrl = System.getenv("DB_URL");
            }

            String jdbcUrl = (envUrl != null && !envUrl.isEmpty()) ? envUrl :
                    (Configuration.config ? prop.getProperty("dbt.url") : prop.getProperty("db.url"));

            String envUser = System.getenv("DB_USERNAME");
            String user = (envUser != null && !envUser.isEmpty()) ? envUser : prop.getProperty("db.name");

            String envPass = System.getenv("DB_PASSWORD");
            String pass = (envPass != null && !envPass.isEmpty()) ? envPass : prop.getProperty("db.password");

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);

            dataSource = new HikariDataSource(config);
        }
        catch (IOException e){
            log.error("SQL Error",e);
            throw new RuntimeException(e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void databaseInit() throws SQLException {
        String createUserTable = """
                CREATE TABLE IF NOT EXISTS users (
                                        username VARCHAR(50) PRIMARY KEY,
                                        password VARCHAR(255) NOT NULL,
                                        salt VARCHAR(255) NOT NULL,
                                        name VARCHAR(100) NOT NULL
                                        );
                """;


        String createAccountTable = """
                CREATE TABLE IF NOT EXISTS accounts(
                id VARCHAR(10) PRIMARY KEY,
                type VARCHAR(50) NOT NULL,
                balance DECIMAL(10,2),
                owner_name VARCHAR(100) NOT NULL,
                FOREIGN KEY (owner_name) REFERENCES users(username) ON DELETE CASCADE
                );
                """;


        String createTransactionTable = """
                CREATE TABLE IF NOT EXISTS transactions(
                id INT PRIMARY KEY AUTO_INCREMENT,
                account_number VARCHAR(10) ,
                to_account VARCHAR(10),
                description VARCHAR(50),
                amount DECIMAL(10,2),
                type VARCHAR(100),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (account_number) REFERENCES accounts(id) ON DELETE CASCADE
                );
                """;



        try(Connection con = DBFunction.getConnection();
            Statement stm = con.createStatement()
        ){
            stm.execute(createUserTable);
            stm.execute(createAccountTable);
            stm.execute(createTransactionTable);

        }
        catch (SQLException e){
            log.error("SQL Error", e);
            throw e;

        }



    }
}
