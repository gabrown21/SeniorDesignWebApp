package edu.bu.metrics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricsTrackerTest {
  private MetricsTracker metricsTracker;

  @BeforeEach
  void setUp() {
    metricsTracker = new MetricsTracker();
  }

  @Test
  void testRecordAndGetUpdatesVolume() {
    metricsTracker.recordUpdate("AAPL");
    metricsTracker.recordUpdate("AAPL");
    metricsTracker.recordUpdate("TSLA");

    String expectedOutput = "Updates-volume:\nAAPL : 2\nTSLA : 1";
    assertEquals(expectedOutput, metricsTracker.getUpdatesVolume());
  }

  @Test
  void testNoUpdates() {
    String expectedOutput = "Updates-volume: No updates received.";
    assertEquals(expectedOutput, metricsTracker.getUpdatesVolume());
  }

  @Test
  void testMultipleSymbolsSorting() {
    metricsTracker.recordUpdate("NVDA");
    metricsTracker.recordUpdate("NVDA");
    metricsTracker.recordUpdate("TSLA");
    metricsTracker.recordUpdate("AAPL");
    metricsTracker.recordUpdate("TSLA");
    metricsTracker.recordUpdate("NVDA");

    String expectedOutput = "Updates-volume:\nNVDA : 3\nTSLA : 2\nAAPL : 1";
    assertEquals(expectedOutput, metricsTracker.getUpdatesVolume());
  }
}
