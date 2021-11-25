import com.example.BankAccountCommand;
import com.example.account.BankAccount;
import com.example.command.CreateAccount;
import com.example.event.BankAccountEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Mock
    CompletionStage<CommandResult<BankAccount>> mockCompletionStage;

    @Mock
    CompletableFuture<CommandResult<BankAccount>> mockCompletableFuture;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    CommandSuccess<BankAccount> getMockEngineResponse(BankAccountCommand command) throws ExecutionException, InterruptedException {

        CommandSuccess<BankAccount> mockCommandSuccess = Mockito.mock(CommandSuccess.class);

        when(mockSurgeEngine.aggregateFor(command.getAccountNumber())).thenReturn(mockAggregate);
        when(mockAggregate.sendCommand(command)).thenReturn(mockCompletionStage);
        when(mockCompletionStage.toCompletableFuture()).thenReturn(mockCompletableFuture);
        when(mockCompletableFuture.get()).thenReturn(mockCommandSuccess);

        return mockCommandSuccess;
    }

    @Test
    void testMockEngine() throws ExecutionException, InterruptedException {
        UUID accountNumber = UUID.randomUUID();
        BankAccount bankAccount = new BankAccount(accountNumber, "Jane Doe", "1234", 1000.0);
        CreateAccount createAccount = new CreateAccount(accountNumber,  bankAccount.accountOwner(), bankAccount.securityCode(), bankAccount.balance());

        CommandSuccess<BankAccount> mockEngineResponse = getMockEngineResponse(createAccount);
        when(mockEngineResponse.aggregateState()).thenReturn(Optional.of(bankAccount));

        Assertions.assertEquals(mockEngineResponse.aggregateState(), Optional.of(bankAccount));
    }
}
