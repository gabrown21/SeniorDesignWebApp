package edu.bu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import edu.bu.handlers.EnqueueingFinhubResponseHandler;
import edu.bu.persistence.SymbolsPersistence;
import edu.bu.persistence.SymbolsPersistenceImpl;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

@Tag("IntegrationTest")
public class PersistenceIntegrationTest {

  EnqueueingFinhubResponseHandler mockEnqueueingFinnhubResponseHandler;

  @BeforeEach
  public void setUp() {
    mockEnqueueingFinnhubResponseHandler = mock(EnqueueingFinhubResponseHandler.class);
  }

  @Test
  public void createRestartConfirm() throws URISyntaxException, InterruptedException {
    SymbolsPersistence symbolsPersistence = new SymbolsPersistenceImpl();

    FinHubWebSocketClient firstFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinhubService.WEBHOOK_URI + "?token=" + FinhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    firstFinHubWebSocketClient.init();

    Logger.info("Removing ORCL to ensure clean slate");
    firstFinHubWebSocketClient.removeSymbol("ORCL");

    assertFalse(firstFinHubWebSocketClient.subscribedSymbols().contains("ORCL"));

    firstFinHubWebSocketClient.addSymbol("ORCL");
    firstFinHubWebSocketClient.closeBlocking();

    FinHubWebSocketClient secondFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinhubService.WEBHOOK_URI + "?token=" + FinhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    secondFinHubWebSocketClient.init();

    assertTrue(secondFinHubWebSocketClient.subscribedSymbols().contains("ORCL"));

    secondFinHubWebSocketClient.close();
  }
}
