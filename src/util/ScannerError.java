package util;

import java.util.Scanner;

public class ScannerError {
    public static Scanner scanner = new Scanner(System.in);
    public static String inputCheck(String fieldInput, String input){


        while(fieldInput.isEmpty()){
            System.out.println("Input is empty!!");
            System.out.print("Please " + input);
            fieldInput = scanner.nextLine();

        }
        return fieldInput;
    }
}
