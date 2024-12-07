package edu.bu.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/** This will calculate metrics for the StockApp */
public class MetricsTracker {
  private final Map<String, Integer> symbolUpdateCounts = new HashMap<>();
  private final Map<String, Integer> priceRequestCounts = new HashMap<>();
  private final ConcurrentMap<String, Integer> finhubUpdates;

  public MetricsTracker() {
    finhubUpdates = new ConcurrentHashMap<>();
  }

  public synchronized void incrementFinhubUpdate(String symbol) {
    finhubUpdates.merge(symbol, 1, Integer::sum);
  }

  public synchronized int getFinhubUpdateCount(String symbol) {
    return finhubUpdates.getOrDefault(symbol, 0);
  }

  public void recordUpdate(String symbol) {
    symbolUpdateCounts.put(symbol, symbolUpdateCounts.getOrDefault(symbol, 0) + 1);
  }

  public String getUpdatesVolume() {
    if (symbolUpdateCounts.isEmpty()) {
      return "Updates-volume: No updates received.";
    }

    return "Updates-volume:\n"
        + symbolUpdateCounts.entrySet().stream()
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
            .map(entry -> entry.getKey() + " : " + entry.getValue())
            .collect(Collectors.joining("\n"));
  }

  public void recordPriceRequest(String symbol) {
    priceRequestCounts.put(symbol, priceRequestCounts.getOrDefault(symbol, 0) + 1);
  }

  public String getPriceVolume() {
    if (priceRequestCounts.isEmpty()) {
      return "Price-volume: No price requests received.";
    }

    return "Price-volume:\n"
        + priceRequestCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .map(e -> e.getKey() + " : " + e.getValue())
            .collect(Collectors.joining("\n"));
  }
}
