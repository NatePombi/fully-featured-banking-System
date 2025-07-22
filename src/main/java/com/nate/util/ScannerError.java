package com.nate.util;

import java.util.Scanner;

public class ScannerError {
    public  Scanner scanner = new Scanner(System.in);
    public ScannerError(Scanner scanner){
        this.scanner = scanner;
    }
    public String inputCheck(String fieldInput, String input){


        while(fieldInput.isEmpty()){
            System.out.println("Input is empty!!");
            System.out.print("Please " + input);
            fieldInput = scanner.nextLine();

        }
        return fieldInput;
    }
}
