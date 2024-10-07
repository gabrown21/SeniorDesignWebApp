package edu.bu.finhub;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import org.tinylog.Logger;

/**
 * A finhub client that returns data frmo "canned" responses rather than from "real" WebSocket
 * updates. This is useful for testing and local development purposes where having predictable,
 * repeatable, deterministic behavior is appealing.
 */
public class MockFinhubClient implements StockUpdatesClient {
  static final String MS_BETWEEN_CALLS_ARGUMENT = "msBetweenCalls";
  static final long MS_BETWEEN_CALLS_DEFAULT = 5000;
  // the time of these responses is not used - this mock will generate timestamps mimicing the time
  // at which it is executed
  static final List<CannedResponse> CANNED_RESPONSES =
      ImmutableList.of(
          new CannedResponse(18.0, 8),
          new CannedResponse(42.0, 15),
          new CannedResponse(71.23, 20),
          new CannedResponse(72.10, 8),
          new CannedResponse(41.88, 10),
          new CannedResponse(18.5, 30),
          new CannedResponse(18.7, 10),
          new CannedResponse(18.1, 11),
          new CannedResponse(41.91, 3),
          new CannedResponse(41.11, 12),
          new CannedResponse(73.0, 11),
          new CannedResponse(19.0, 7),
          new CannedResponse(71.8, 30),
          new CannedResponse(43.1, 10));
  final DataStore store;
  final Set<String> arguments;

  final Set<String> subscribedSymbols;

  // these variables help us round-robin canned responses through known stock symbols
  List<String> symbolOrder;
  int responsesCount = 0;

  public MockFinhubClient(DataStore store, Set<String> arguments) {
    this.store = store;
    this.arguments = arguments;

    this.subscribedSymbols = new ConcurrentSkipListSet<>();

    // start with some subscribed symbols in deterministic order to support tests that won't modify
    // subscribed set
    addSymbol("RIVN");
    addSymbol("BAC");
    addSymbol("NKE");
    symbolOrder = List.of("RIVN", "BAC", "NKE");
  }

  @Override
  public void connect() {
    new Thread(
            () -> {
              long msBetweenCalls = parseMsBetweenCalls();
              Logger.info(
                  "Starting mock finhub client with " + msBetweenCalls + " ms between each call.");

              Instant responseTime = Instant.now();

              for (CannedResponse cannedResponse : CANNED_RESPONSES) {
                if (symbolOrder.isEmpty()) {
                  Logger.warn(
                      "Mock Finhub Client cannot emit a canned response because there are zero symbols registered.");
                } else {
                  String nextSymbol = symbolOrder.get(responsesCount % symbolOrder.size());

                  FinhubResponse actualResponse =
                      new FinhubResponse(
                          nextSymbol,
                          cannedResponse.price,
                          responseTime.toEpochMilli(),
                          cannedResponse.volume);

                  Logger.info("processing mock response: " + actualResponse);
                  store.update(ImmutableList.of(actualResponse));

                  responsesCount++;
                  responseTime = responseTime.plus(1, ChronoUnit.SECONDS);
                }

                try {
                  Thread.sleep(msBetweenCalls);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .start();
  }

  long parseMsBetweenCalls() {
    for (String argument : arguments) {
      if (argument.startsWith(MS_BETWEEN_CALLS_ARGUMENT)) {
        String stringVal = argument.split(MS_BETWEEN_CALLS_ARGUMENT + "=")[1];
        return Long.valueOf(stringVal);
      }
    }

    return MS_BETWEEN_CALLS_DEFAULT;
  }

  @Override
  public void addSymbol(String symbol) {
    subscribedSymbols.add(symbol);
    symbolOrder = subscribedSymbols.stream().collect(Collectors.toList());
  }

  public void removeSymbol(String symbol) {
    subscribedSymbols.remove(symbol);
    symbolOrder = subscribedSymbols.stream().collect(Collectors.toList());
  }

  public Set<String> subscribedSymbols() {
    return Collections.unmodifiableSet(subscribedSymbols);
  }

  /** Simple pair of price and volume used to configure canned responses. */
  static class CannedResponse {
    final double price;
    final long volume;

    public CannedResponse(double price, long volume) {
      this.price = price;
      this.volume = volume;
    }
  }
}
