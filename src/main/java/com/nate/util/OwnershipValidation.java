package com.nate.util;

import com.nate.model.IUser;
import com.nate.service.ISessionService;

public class OwnershipValidation {

    private final ISessionService service;

    public OwnershipValidation(ISessionService service){
        this.service = service;
    }
    //checking if the current logged-in user is the owner of the account with specified account number
    public boolean isOwner(String accountNumber){

        IUser user = service.getCurrentUser();
        return user.getAccounts().stream()
                .anyMatch(acc-> acc.getAccountNumber().equals(accountNumber));

    }
}
