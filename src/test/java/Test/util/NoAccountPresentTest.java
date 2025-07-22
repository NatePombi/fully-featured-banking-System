package Test.util;

import com.nate.model.Account;
import com.nate.model.IAccount;
import com.nate.model.IUser;
import com.nate.model.User;
import com.nate.service.SessionService;
import com.nate.util.NoAccountPresent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoAccountPresentTest {

    @Mock
    SessionService mockService;

    private NoAccountPresent noAccountPresent;

    @BeforeEach
    void startUp(){
        noAccountPresent = new NoAccountPresent(mockService);
    }

    @Test
    void testNoAccount(){
        IAccount account = mock(Account.class);
        IUser user = mock(User.class);
        List<IAccount> accountList = List.of(account);
        when(mockService.getCurrentUser()).thenReturn(user);
        when(mockService.getCurrentUser().getAccounts()).thenReturn(accountList);

        boolean validate = noAccountPresent.noAccount();

        assertTrue(validate);
    }

    @Test
    void testNoAccount_Fail(){
        IUser user = mock(User.class);
        when(mockService.getCurrentUser()).thenReturn(user);

        boolean validate = noAccountPresent.noAccount();

        assertFalse(validate);
    }

}
