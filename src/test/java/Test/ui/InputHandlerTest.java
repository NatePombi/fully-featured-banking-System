package Test.ui;

import com.nate.ui.InputHandler;
import com.nate.util.PromptMessage;
import com.nate.util.ScannerError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputHandlerTest {
    @Mock
    Scanner mockScanner;

    private ScannerError scannerError;

    private PromptMessage message;
    private InputHandler inputHandler;

    @BeforeEach
    void startUp(){
        scannerError = new ScannerError(mockScanner);
        scannerError.scanner = mockScanner;
        message = Mockito.spy(new PromptMessage(scannerError));
        inputHandler = new InputHandler(mockScanner,scannerError);
    }

    @Test
    void testPromptName(){
        when(mockScanner.nextLine()).thenReturn("Test One");

        String name = inputHandler.promptName();

        assertEquals("Test One",name,"should have a string value of Test One");
    }

    @Test
    void testPromptPassword(){
        when(mockScanner.nextLine()).thenReturn("test123");

        String name = inputHandler.promptPassword();

        assertEquals("test123",name,"should have a string value of test123");
    }

    @Test
    void testPromptUsername(){
        when(mockScanner.nextLine()).thenReturn("test");

        String name = inputHandler.promptUserName();

        assertEquals("test",name,"should have a string value of test");
    }

    @Test
    void testPromptAccountNumber(){
        when(mockScanner.nextLine()).thenReturn("0123456789");


        String name = message.promptInt(any());

        assertEquals("0123456789",name,"should have a string value of 0123456789");
    }

    @Test
    void testPromptAccountNumberSendingFunds(){
        when(mockScanner.nextLine()).thenReturn("0987654321");


        String name = message.promptString(any());

        assertEquals("0987654321",name,"should have a string value of 0987654321");
    }

    @Test
    void testPromptAccountNumberReceivingFunds(){
        when(mockScanner.nextLine()).thenReturn("1234567890");


        String name = message.promptString(any());

        assertEquals("1234567890",name,"should have a string value of 1234567890");
    }

    @Test
    void testPromptMenuSelection(){
        when(scannerError.scanner.nextLine()).thenReturn("1");

        int num = inputHandler.promptMenuSelection();

        assertEquals(1, num,"should have an int value of 1");
    }
}
