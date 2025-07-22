package Test.util;

import com.nate.util.PromptMessage;
import com.nate.util.ScannerError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromptMessageTest {
    @Mock
    ScannerError scannerError;

    private PromptMessage message;

    private Scanner scanner;

    @BeforeEach
    void startUp(){
        scanner = mock(Scanner.class);
        scannerError.scanner = scanner;
        message = new PromptMessage(scannerError);
    }

    @Test
    void testBigDec(){
        when(scanner.nextBigDecimal()).thenReturn(new BigDecimal("100.00"));

        BigDecimal num = message.promptBigDec("Enter: ");

        assertEquals(new BigDecimal("100.00"),num,"should have a BigDecimal value of 100.00");
    }


    @Test
    void testBigIntRec(){
        when(scanner.nextLine()).thenReturn("1234567890");

        String accNum = message.promptIntRec("Enter: ");

        assertEquals("1234567890",accNum,"Should have the same account number");
    }
}
