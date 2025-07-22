package com.nate.ui;

import com.nate.service.ISessionService;

public class MenuRenderer {
    private final ISessionService service;

    public MenuRenderer(ISessionService service){
        this.service = service;
    }
    //For Start up menu display
    public void showStartUpMenu(){
        System.out.println("""
                    Welcome to Maze Bank
                  --------------------------
                  1) Register new User
                  2) Login
                  3) Exit
                """);
    }

    //`For banking menu display
    public void showBankingMenu(){
        System.out.printf("""
                       \s
                        Welcome: %s
                    ---------------------
                    1) Open New Account
                    2) View Account Balance
                    3) Deposit Funds
                    4) Withdraw Funds
                    5) Transfer Funds
                    6) View Transaction History
                    7) View Account Details
                    8) View All Accounts
                    9) Delete Account
                    10) Log out
                %n""", service.getCurrentUser().getUsername());
    }


}
