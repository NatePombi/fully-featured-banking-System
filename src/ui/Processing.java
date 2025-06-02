package ui;

public class Processing {

    //for the animated ellipses while processing
    public static void processing() throws InterruptedException {
        System.out.print("Processing");

        for(int i = 0; i<3;i++){
            System.out.print(".");
            Thread.sleep(500);
        }
    }
}
