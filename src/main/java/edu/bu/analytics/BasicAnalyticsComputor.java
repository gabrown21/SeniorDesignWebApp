package edu.bu.analytics;

import edu.bu.data.DataStore;
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
}
