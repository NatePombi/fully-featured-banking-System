package com.nate.util;

import java.time.LocalDateTime;

public class DateTimeFormat {
    public static String dateFormatter(LocalDateTime localDateTime){
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
        return localDateTime.format(formatter);
    }
}
