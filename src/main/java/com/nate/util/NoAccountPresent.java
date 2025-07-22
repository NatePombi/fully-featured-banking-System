package com.nate.util;

import com.nate.service.ISessionService;
import com.nate.service.SessionService;

public class NoAccountPresent {

    private  ISessionService service;
    public NoAccountPresent(ISessionService service){
        this.service = service;
    }

    //when a user wants to make an action in the bank menu without having an account
    public boolean noAccount(){
        if (service.getCurrentUser().getAccounts().isEmpty()){
            System.out.println("No Accounts Present: First create an account");
            return false;
        }

        return true;
    }
}
