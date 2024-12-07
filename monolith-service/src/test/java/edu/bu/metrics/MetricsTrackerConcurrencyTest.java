package edu.bu.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetricsTrackerConcurrencyTest {
  MetricsTracker metricsTracker;

  @BeforeEach
  public void setUp() {
    metricsTracker = new MetricsTracker();
  }

  @Test
  public void concurrentUpdatesCountAccess() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(100);
    List<Callable<Boolean>> tasks = new ArrayList<>();

    for (int a = 0; a < 100; a++) {
      tasks.add(
          () -> {
            metricsTracker.incrementFinhubUpdate("TEST");
            return true;
          });
    }

    executorService.invokeAll(tasks);

    assertEquals(100, metricsTracker.getFinhubUpdateCount("TEST"));
  }
}
