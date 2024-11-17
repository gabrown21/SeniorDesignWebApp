package edu.bu;

import static org.junit.jupiter.api.Assertions.*;

import edu.bu.persistence.StoredSymbol;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FinHubWebSocketClientTest {

  @Test
  public void testFilterRecentSymbols() {
    Instant now = Instant.now();
    StoredSymbol recentSymbol1 = new StoredSymbol("AAPL", now.minus(Duration.ofDays(5)));
    StoredSymbol recentSymbol2 = new StoredSymbol("RIVN", now.minus(Duration.ofDays(1)));
    StoredSymbol staleSymbol = new StoredSymbol("BAC", now.minus(Duration.ofDays(15)));

    Set<StoredSymbol> testSymbols =
        Stream.of(recentSymbol1, recentSymbol2, staleSymbol).collect(Collectors.toSet());

    FinHubWebSocketClient client = Mockito.mock(FinHubWebSocketClient.class);
    Mockito.when(client.filterRecentSymbols(Mockito.any())).thenCallRealMethod();

    Set<String> filteredSymbols = client.filterRecentSymbols(testSymbols);
    Set<String> expectedSymbols = Set.of("AAPL", "RIVN");
    assertEquals(expectedSymbols, filteredSymbols);
  }
}
