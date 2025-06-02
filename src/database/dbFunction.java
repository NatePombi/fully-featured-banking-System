package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbFunction {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:h2:./data/bankingdbz";
        String name = "bnk";
        String password = "";

        return DriverManager.getConnection(url,name,password);
    }

    public static void databaseInit(){
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



        try(Connection con =dbFunction.getConnection();
            Statement stm = con.createStatement()
        ){
            stm.execute(createUserTable);
            stm.execute(createAccountTable);
            stm.execute(createTransactionTable);

        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }



    }
}
