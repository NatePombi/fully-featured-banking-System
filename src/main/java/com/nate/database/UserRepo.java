package com.nate.database;

import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.model.IUser;
import com.nate.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepo {


    public IUser reloadUser(String username){

        try(Connection con = DBFunction.getConnection()){
            String getUser = "SELECT * FROM users WHERE username = ?";
            IUser user = null;

            try(PreparedStatement stm = con.prepareStatement(getUser)){
                stm.setString(1,username);

                try(ResultSet rs = stm.executeQuery()){
                    while (rs.next()){
                       String name = rs.getString("name");
                       String salt = rs.getString("salt");
                       String pass = rs.getString("password");

                        user = new User(username,pass,name,salt);
                    }
                }
            }


            if(user != null){
                String accAdd = "SELECT * FROM accounts WHERE owner_name = ?";

                try(PreparedStatement stm = con.prepareStatement(accAdd)){
                    stm.setString(1,username);

                    try(ResultSet rs = stm.executeQuery()){
                        while(rs.next()){
                            String accNum = rs.getString("id");
                            String type = rs.getString("type");
                            String name = rs.getString("owner_name");
                            BigDecimal balance = rs.getBigDecimal("balance");

                            IAccount account = new Account(balance,type,name,accNum);
                            user.addAccount(account);
                        }
                    }
                }


            }

            return user;

        }
        catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }
}
