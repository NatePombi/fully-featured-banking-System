package util;

import java.security.SecureRandom;

public class AutoIdGenerator {

    public static String generateId(){
        String characters = "0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder builder = new StringBuilder(10);

        for (int i = 0; i<10; i++){
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return builder.toString();
    }
}
