package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

public class CurrencyFormatter {

    //formatting the amount into local currency
    public static String getCurrency(BigDecimal amount){
        if(amount.compareTo(BigDecimal.ZERO)<0){
            throw new NoSuchElementException();
        }

        return "R" + amount;
    }
}
