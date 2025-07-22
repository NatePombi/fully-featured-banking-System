package com.nate.ui;

import com.nate.util.PromptMessage;
import com.nate.util.ScannerError;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;
    private final ScannerError scannerError;

    public InputHandler(Scanner scanner,ScannerError scannerError){
        this.scanner = scanner;
        this.scannerError = scannerError;

    }

    // Prompts the user to select a menu option and ensures it is an integer
    public int promptMenuSelection(){
        int input;
        while(true){
            System.out.println("Enter a number from the above menu.");
            try{
                input = Integer.parseInt(scannerError.scanner.nextLine());
                break;
            }
            catch (NumberFormatException ex) {
                System.out.println("Invalid selection. Please choose a number from the menu.");
            }
        }
        return input;
    }


    public String promptName(){
        System.out.print("Enter name: ");
        return scannerError.inputCheck(scanner.nextLine(),"Enter name: ");
    }

    public String promptUserName(){
        System.out.print("Enter username: ");
        return scannerError.inputCheck(scanner.nextLine(),"Enter username: ");
    }

    public String promptPassword(){
        System.out.print("Enter password: ");
        return scannerError.inputCheck(scanner.nextLine(),"Enter password: ");
    }

    // Used when asking for account type input (e.g., checking, savings, business)
    public String emptyInputOrSpellingError(){
        System.out.print("Enter the account type shown [checking,savings,business]: ");
        return scannerError.scanner.nextLine();
    }


}
