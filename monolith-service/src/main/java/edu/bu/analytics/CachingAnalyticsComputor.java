package edu.bu.analytics;

import edu.bu.data.DataStore;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Implementors TotalObservedVolume with caching */
public class CachingAnalyticsComputor implements AnalyticsComputor {
  final BasicAnalyticsComputor basicAnalyticsComputor;
  final DataStore dataStore;

  /** Stock symbol keyed hash of total observed volume results. */
  Map<String, Long> totalObservedVolumeCache = new HashMap<>();

  // leaving the rest for you to implement, but here is an example of how to
  // delegate
  private int cacheInvalidationVersion;

  public CachingAnalyticsComputor(DataStore dataStore) {
    this.basicAnalyticsComputor = new BasicAnalyticsComputor(dataStore);
    this.dataStore = dataStore;
    this.cacheInvalidationVersion = dataStore.numberOfUpdatesSeen();
  }

  @Override
  public Set<String> knownSymbols() {
    return basicAnalyticsComputor.knownSymbols();
  }

  @Override
  public long totalObservedVolume(String symbol) throws UnknownSymbolException {
    int currentUpdateVersion = dataStore.numberOfUpdatesSeen();
    if (cacheInvalidationVersion != currentUpdateVersion) {
      totalObservedVolumeCache.clear();
      cacheInvalidationVersion = currentUpdateVersion;
    }
    if (totalObservedVolumeCache.containsKey(symbol)) {
      return totalObservedVolumeCache.get(symbol);
    }

    long volume = basicAnalyticsComputor.totalObservedVolume(symbol);
    totalObservedVolumeCache.put(symbol, volume);
    return volume;
  }

  @Override
  public String mostActiveStock() {
    return basicAnalyticsComputor.mostActiveStock();
  }

  @Override
  public double currentPrice(String symbol) throws UnknownSymbolException {
    return basicAnalyticsComputor.currentPrice(symbol);
  }

  @Override
  public String mostActiveStock(Instant startTime, Instant endTime) {
    return basicAnalyticsComputor.mostActiveStock(startTime, endTime);
  }

  @Override
  public Double averageVolumePerSecond(String symbol) throws UnknownSymbolException {
    return basicAnalyticsComputor.averageVolumePerSecond((symbol));
  }
}
