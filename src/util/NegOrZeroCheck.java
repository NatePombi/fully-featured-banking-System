package util;

import java.math.BigDecimal;

public class NegOrZeroCheck {
    public static boolean isNegativeOrZero(BigDecimal amount){
        return amount.compareTo(BigDecimal.ZERO)<=0;
    }

    public static boolean isNegative(BigDecimal amount1 ,BigDecimal amount2){
        return amount1.compareTo(amount2)<0;
    }
}
