package ui;

import util.Animate;

public class SystemMessage {
   //for header display
    public static void showHeader(String title){
        System.out.println("\n" + title);
        System.out.println("-".repeat(20));
    }

    //For animated ellipses during goodbye
    public static void showGoodBye() throws InterruptedException {
        Animate.animate("Exiting");
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        System.out.print("Goodbye");
    }

    //For animated ellipses during logging out
    public static void showLogOut() throws InterruptedException {
        Animate.animate("Logging out");
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
