import com.example.BankAccountCommand;
import com.example.account.BankAccount;
import com.example.command.CreateAccount;
import com.example.event.BankAccountEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import surge.javadsl.command.AggregateRef;
import surge.javadsl.command.SurgeCommand;
import surge.javadsl.common.CommandResult;
import surge.javadsl.common.CommandSuccess;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

class MockEngineTest {
    @Mock
    SurgeCommand<UUID, BankAccount, BankAccountCommand, ?, BankAccountEvent> mockSurgeEngine;

    @Mock
    AggregateRef<BankAccount, BankAccountCommand, BankAccountEvent> mockAggregate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMockEngine() {
        UUID accountNumber = UUID.randomUUID();

        when(mockSurgeEngine.aggregateFor(accountNumber)).thenReturn(mockAggregate);

        CreateAccount createAccount = new CreateAccount(accountNumber, "Jane Doe", "1234", 1000.0);
        BankAccount expectedBankAccount = new BankAccount(accountNumber, createAccount.accountOwner(), createAccount.securityCode(), createAccount.initialBalance());
        CommandSuccess<BankAccount> commandResponse = new CommandSuccess(Optional.of(expectedBankAccount));
        CompletionStage<CommandResult<BankAccount>> bankAccountCompletionStage = CompletableFuture.completedFuture(commandResponse);

        when(mockSurgeEngine.aggregateFor(accountNumber).sendCommand(createAccount)).thenReturn(bankAccountCompletionStage);

        Assertions.assertEquals(mockSurgeEngine.aggregateFor(accountNumber).sendCommand(createAccount), bankAccountCompletionStage);
    }
}
