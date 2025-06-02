package util;

public class Animate {
    //for the animated effect
    public static void animate(String message) throws InterruptedException {
        System.out.print(message);
        for(int i =0; i<3;i++){
            Thread.sleep(500);
            System.out.print(".");
        }
        System.out.println();
    }
}
