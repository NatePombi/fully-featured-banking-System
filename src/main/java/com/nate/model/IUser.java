package com.nate.model;

import java.util.List;
import java.util.Objects;

public interface IUser {

    String getUsername();

    String getPassword();

    String getName();

    String getSalt();

    void addAccount(IAccount account);

    List<IAccount> getAccounts();

     boolean equals(Object o);

     int hashCode();

     String toString();

     void removeAccount(String accountNumber);
}

