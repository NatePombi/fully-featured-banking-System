package util;

public class ExceptionHandler {

    public ExceptionHandler(){

    }
    public void handleException(Exception e){
        System.out.println("Error: " + e.getMessage());
    }
}
