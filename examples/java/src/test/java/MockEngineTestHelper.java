import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import surge.javadsl.command.AggregateRef;
import surge.javadsl.command.SurgeCommand;
import surge.javadsl.common.CommandResult;
import surge.javadsl.common.CommandSuccess;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;

public class MockEngineTestHelper<AggId, Agg, Command, Rej, Event> {

    SurgeCommand<AggId, Agg, Command, Rej, Event> mockSurgeEngine = Mockito.mock(SurgeCommand.class);
    AggregateRef<Agg, Command, Event> mockAggregate = Mockito.mock(AggregateRef.class);
    CompletionStage<CommandResult<Agg>> mockCompletionStage = Mockito.mock(CompletionStage.class);
    CompletableFuture<CommandResult<Agg>> mockCompletableFuture = Mockito.mock(CompletableFuture.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    CommandSuccess<Agg> getMockEngineResponse(Command command, AggId accountNum) throws ExecutionException, InterruptedException {

        CommandSuccess<Agg> mockCommandSuccess = Mockito.mock(CommandSuccess.class);

        when(mockSurgeEngine.aggregateFor(accountNum)).thenReturn(mockAggregate);
        when(mockAggregate.sendCommand(command)).thenReturn(mockCompletionStage);
        when(mockCompletionStage.toCompletableFuture()).thenReturn(mockCompletableFuture);
        when(mockCompletableFuture.get()).thenReturn(mockCommandSuccess);

        return mockCommandSuccess;
    }

}
