package Test.util;

import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.service.SessionService;
import com.nate.util.OwnershipValidation;
import com.nate.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OwnershipValidationTest {

    @Mock
    SessionService service;

    private OwnershipValidation ownershipValidation;

    @BeforeEach
    void startUp(){
        ownershipValidation = new OwnershipValidation(service);
    }

    @Test
    void testIsOwner(){
        IUser mockUser = mock(User.class);
        IAccount mockAccount = mock(Account.class);
        List<IAccount> accounts = List.of(mockAccount);

        when(service.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getAccounts()).thenReturn(accounts);
        when(mockAccount.getAccountNumber()).thenReturn("1234567890");

        boolean validate = ownershipValidation.isOwner("1234567890");

        assertTrue(validate);
    }


    @Test
    void testIsOwner_Fail(){
        IUser mockUser = mock(User.class);
        IAccount mockAccount = mock(Account.class);
        List<IAccount> accounts = List.of(mockAccount);

        when(service.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getAccounts()).thenReturn(accounts);
        when(mockAccount.getAccountNumber()).thenReturn("1234567890");

        boolean validate = ownershipValidation.isOwner("0987654321");

        assertFalse(validate);
    }
}
