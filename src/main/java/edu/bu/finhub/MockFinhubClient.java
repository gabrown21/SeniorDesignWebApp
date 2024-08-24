package edu.bu.finhub;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
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
  // at which
  // it is executed
  static final List<FinhubResponse> CANNED_RESPONSES =
      ImmutableList.of(
          new FinhubResponse("RIVN", 18.0, 0, 8),
          new FinhubResponse("BAC", 42.0, 0, 15),
          new FinhubResponse("NKE", 71.23, 0, 20),
          new FinhubResponse("NKE", 72.10, 0, 8),
          new FinhubResponse("BAC", 41.88, 0, 10),
          new FinhubResponse("RIVN", 18.5, 0, 30),
          new FinhubResponse("RIVN", 18.7, 0, 10),
          new FinhubResponse("RIVN", 18.1, 0, 11),
          new FinhubResponse("BAC", 41.91, 0, 3),
          new FinhubResponse("BAC", 41.11, 0, 12),
          new FinhubResponse("NKE", 73.0, 0, 11),
          new FinhubResponse("RIVN", 19.0, 0, 7),
          new FinhubResponse("NKE", 71.8, 0, 30),
          new FinhubResponse("BAC", 43.1, 0, 10));
  final DataStore store;
  final Set<String> arguments;

  public MockFinhubClient(DataStore store, Set<String> arguments) {
    this.store = store;
    this.arguments = arguments;
  }

  @Override
  public void connect() {
    new Thread(
            () -> {
              long msBetweenCalls = parseMsBetweenCalls();
              Logger.info(
                  "Starting mock finhub client with " + msBetweenCalls + " ms between each call.");

              Instant responseTime = Instant.now();

              for (FinhubResponse response : CANNED_RESPONSES) {
                FinhubResponse actualResponse =
                    new FinhubResponse(
                        response.symbol,
                        response.price,
                        responseTime.toEpochMilli(),
                        response.volume);

                Logger.info("processing mock response: " + actualResponse);
                store.update(ImmutableList.of(actualResponse));

                responseTime = responseTime.plus(1, ChronoUnit.SECONDS);

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
}
