package edu.bu.analytics;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The analytics computer relies on the {@link DataStore} for historic stock price and volume data
 * points. It uses these data points to compute higher level metrics about individual as well as
 * aggregate stocks.
 */
public class BasicAnalyticsComputor implements AnalyticsComputor {
  // exposes historic data points for the computations performed here
  final DataStore dataStore;

  /** Construct an instance by providing the {@link DataStore} collaborator. */
  public BasicAnalyticsComputor(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public Set<String> knownSymbols() {
    return dataStore.knownSymbols();
  }

  @Override
  public long totalObservedVolume(String symbol) throws UnknownSymbolException {
    checkSymbolExists(symbol);

    return dataStore.getHistory(symbol).stream()
        .collect(Collectors.summingLong(data -> data.volume));
  }

  @Override
  public String mostActiveStock() {
    Map<String, Long> totalVolumeBySymbol =
        dataStore.knownSymbols().stream()
            .collect(
                // Collectors.toMap takes two lambdas (functions) one that transforms input to the
                // keys of the map
                // and another that transforms the input to the values of the map.
                Collectors.toMap(
                    symbol -> symbol,
                    symbol ->
                        dataStore.getHistory(symbol).stream()
                            .collect(Collectors.summingLong(data -> data.volume))));

    Optional<Map.Entry<String, Long>> result =
        totalVolumeBySymbol.entrySet().stream().max(Map.Entry.comparingByValue());

    return result.isPresent() ? result.get().getKey() : null;
  }

  /**
   * @param symbol stock symbol of interest
   * @return the most recently reported price for the stock
   * @throws UnknownSymbolException if the requested stock symbol has not been seen by the data
   *     store
   */
  public double currentPrice(String symbol) throws UnknownSymbolException {
    checkSymbolExists(symbol);

    return dataStore.getHistory(symbol).get(0).price;
  }

  /**
   * Internal helper for checking whether a given stock symbol has been seen by our data store.
   *
   * @param symbol stock symbol of interest
   * @throws UnknownSymbolException if the data store is not aware of the requested symbol.
   */
  void checkSymbolExists(String symbol) throws UnknownSymbolException {
    if (!dataStore.haveSymbol(symbol)) {
      throw new UnknownSymbolException(symbol);
    }
  }

  /**
   * @param startTime inclusive start of the window of interest
   * @param endTime inclusive end of the window of interest
   * @return of all the stocks that the data store is aware of, the symbol of the stock that has
   *     recorded the highest total trade volume between startTime and endTime, inclusive.
   */
  public String mostActiveStock(Instant startTime, Instant endTime) {
    long mostVolume = 0;
    String mostActive = null;

    for (String symbol : dataStore.knownSymbols()) {
      long totalVolume = 0;
      List<FinhubResponse> dataHistory = dataStore.getHistory(symbol);

      for (FinhubResponse response : dataHistory) {
        Instant tradeTime = Instant.ofEpochMilli(response.msSinceEpoch);
        if (!tradeTime.isBefore(startTime) && !tradeTime.isAfter(endTime)) {
          totalVolume += response.volume;
        }
      }
      if (totalVolume > mostVolume) {
        mostActive = symbol;
        mostVolume = totalVolume;
      }
    }
    return mostActive;
  }

  /**
   * Helper function for calculating time interval
   *
   * @param history
   * @return maxTime- minTime
   */
  private long calculateTimeInterval(List<FinhubResponse> history) {
    long minTime = history.stream().mapToLong(data -> data.msSinceEpoch).min().orElseThrow();

    long maxTime = history.stream().mapToLong(data -> data.msSinceEpoch).max().orElseThrow();

    long timeInterval = maxTime - minTime;
    if (timeInterval == 0) {
      return 1000;
    }
    return timeInterval;
  }

  /**
   * Caclulates average trading colume per second on a given symbol
   *
   * @param symbol
   * @return a string representing the average volume per second for the given symbol
   */
  public String averageVolumePerSecond(String symbol) throws UnknownSymbolException {
    checkSymbolExists(symbol);

    List<FinhubResponse> history = dataStore.getHistory(symbol);

    long totalVolume = totalObservedVolume(symbol);
    long timeInterval = calculateTimeInterval(history);
    double averageVolumePerSecond = (double) totalVolume / (timeInterval / 1000.0);

    return String.format("%.2f", averageVolumePerSecond);
  }
}
