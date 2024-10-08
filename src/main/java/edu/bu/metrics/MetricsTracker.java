package edu.bu.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** This will calculate metrics for the StockApp */
public class MetricsTracker {
  private final Map<String, Integer> symbolUpdateCounts = new HashMap<>();

  public void recordUpdate(String symbol) {
    symbolUpdateCounts.put(symbol, symbolUpdateCounts.getOrDefault(symbol, 0) + 1);
  }

  public String getUpdatesVolume() {
    if (symbolUpdateCounts.isEmpty()) {
      return "Updates-volume: No updates received.";
    }

    StringBuilder result = new StringBuilder("Updates-volume:\n");

    result.append(
        symbolUpdateCounts.entrySet().stream()
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
            .map(entry -> entry.getKey() + " : " + entry.getValue())
            .collect(Collectors.joining("\n")));

    return result.toString();
  }
}
