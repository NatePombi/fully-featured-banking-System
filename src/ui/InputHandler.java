package ui;

import util.PromptMessage;
import util.ScannerError;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    // Prompts the user to select a menu option and ensures it is an integer
    public static int promptMenuSelection(){
        int input;
        while(true){
            System.out.println("Enter a number from the above menu.");
            try{
                input = Integer.parseInt(ScannerError.scanner.nextLine());
                break;
            }
            catch (NumberFormatException ex) {
                System.out.println("Invalid selection. Please choose a number from the menu.");
            }
        }
        return input;
    }


    public static String promptName(){
        System.out.print("Enter name: ");
        return ScannerError.inputCheck(scanner.nextLine(),"Enter name: ");
    }

    public static String promptUserName(){
        System.out.print("Enter username: ");
        return ScannerError.inputCheck(scanner.nextLine(),"Enter username: ");
    }

    public static String promptPassword(){
        System.out.print("Enter password: ");
        return ScannerError.inputCheck(scanner.nextLine(),"Enter password: ");
    }

    // Used when asking for account type input (e.g., checking, savings, business)
    public static String emptyInputOrSpellingError(){
        System.out.print("Enter the account type shown [checking,savings,business]: ");
        return ScannerError.scanner.nextLine();
    }

    // Asks for account number
    public static String promptAccountNumber(){
        return PromptMessage.promptInt("Enter account number of the account: ");
    }

    // Used specifically for transfers (sending side)
    public static String promptAccountNumberSendingFunds(){
        return PromptMessage.promptString("Enter account number of the account you transferring from: ");
    }

    // Used specifically for transfers (receiving side)
    public static String promptAccountNumberReceivingFunds(){
        return PromptMessage.promptString("Enter account number of the account you want to transfer to: ");
    }

    // Prompts for an amount (used in deposits, withdrawals, transfers)
    public static BigDecimal promptAmount(String message){
        return PromptMessage.promptBigDec(message);
    }
}
