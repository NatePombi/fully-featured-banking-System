package util;



import java.math.BigDecimal;
import java.util.InputMismatchException;

public class PromptMessage {

    // Prompts the user for a BigDecimal input and validates it
    public static BigDecimal promptBigDec(String message){
      BigDecimal input;
        while(true){
            System.out.print(message);
            try{
               input = ScannerError.scanner.nextBigDecimal();
               ScannerError.scanner.nextLine();
               break;
            }
            catch (NumberFormatException | InputMismatchException ex){
                System.out.println("Invalid number!: Please enter a valid number");
                ScannerError.scanner.nextLine();
            }

        }

        return input;
    }

    // Prompts the user for a non-blank string input
    public static String promptString(String message){
        String input;

        while(true){
            System.out.print(message);

            input = ScannerError.scanner.nextLine();
            if(input.isBlank()){
                System.out.println("Input is blank!!");
            }
            else {
                break;
            }

        }
        return input;
    }

    // Prompts the user for a 10-digit account number as a String
    public static String promptInt(String message){
        String input;

        while(true) {
            System.out.print(message);

            input = ScannerError.scanner.nextLine();
            if (input.isBlank()) {
                System.out.println("Input is blank!!");
                continue;
            }
            if (input.length() < 10) {
                System.out.println("account number is too short. Must be 10 numbers long");
                continue;
            }

            if (input.length() > 10) {
                System.out.println("account number is too long. Must be 10 numbers long");
                continue;
            }

            if(!isValidInt(input)){
                System.out.println("Alert!: Account number " + input+ " is not valid numbers");
                continue;
            }

            break;

        }
        return input;
    }

    // Checks if the input string is a valid integer
    public static boolean isValidInt(String number){
        return number.matches("-?\\d+");
    }


}
