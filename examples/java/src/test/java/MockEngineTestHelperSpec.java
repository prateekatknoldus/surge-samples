import com.example.BankAccountCommand;
import com.example.account.BankAccount;
import com.example.command.CreateAccount;
import com.example.event.BankAccountEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import surge.javadsl.common.CommandSuccess;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

class MockEngineTestHelperSpec {

    MockEngineTestHelper<UUID, BankAccount, BankAccountCommand, ?, BankAccountEvent> mockEngineTestHelper;

    @Test
    void testMockEngine() throws ExecutionException, InterruptedException {
        mockEngineTestHelper = new MockEngineTestHelper<>();
        UUID accountNumber = UUID.randomUUID();
        BankAccount bankAccount = new BankAccount(accountNumber, "Jane Doe", "1234", 1000.0);
        CreateAccount createAccount = new CreateAccount(accountNumber,  bankAccount.accountOwner(), bankAccount.securityCode(), bankAccount.balance());

        CommandSuccess<BankAccount> mockEngineResponse = mockEngineTestHelper.getMockEngineResponse(createAccount, createAccount.getAccountNumber());
        when(mockEngineResponse.aggregateState()).thenReturn(Optional.of(bankAccount));

        Assertions.assertEquals(mockEngineResponse.aggregateState(), Optional.of(bankAccount));
    }
}
