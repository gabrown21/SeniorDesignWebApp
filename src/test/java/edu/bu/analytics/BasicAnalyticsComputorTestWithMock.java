package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicAnalyticsComputorTestWithMock {

  static final Instant TEST_TIME = Instant.now();
  AnalyticsComputor basicAnalyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = mock();
    basicAnalyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void currentPrice_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> basicAnalyticsComputor.currentPrice("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void currentPrice_singleValue() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(134.12, basicAnalyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void currentPrice_multipleValues() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);

    // recall that getHistory returns responses in Stack order - Last In First Out
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse(
                    "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(135.01, basicAnalyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_uknownSymbol() {
    when(dataStore.haveSymbol("NVDA")).thenReturn(false);

    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> basicAnalyticsComputor.totalObservedVolume("NVDA"));

    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(100, basicAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse(
                    "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(300, basicAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void mostActiveStock_noData() {
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of());

    assertNull(basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA"));
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals("NVDA", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA", "TSLA"));

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100),
                new FinhubResponse(
                    "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    when(dataStore.getHistory("TSLA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("TSLA", 300.00, TEST_TIME.toEpochMilli(), 300)));

    // TSLA 300 > NVDA 200
    assertEquals("TSLA", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA", "TSLA"));

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    when(dataStore.getHistory("TSLA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse("TSLA", 300.00, TEST_TIME.toEpochMilli(), 100),
                new FinhubResponse(
                    "TSLA", 305.00, TEST_TIME.plus(10, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // TSLA twice and 200 > 100
    assertEquals("TSLA", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void knownSymbols_single() {
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA"));

    assertEquals(ImmutableSet.of("NVDA"), basicAnalyticsComputor.knownSymbols());
  }

  @Test
  public void mostActiveStockInWindow_noData() {
    // No known symbols
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of());

    Instant startTime = TEST_TIME.minus(1, ChronoUnit.DAYS);
    Instant endTime = TEST_TIME.plus(1, ChronoUnit.DAYS);

    assertNull(basicAnalyticsComputor.mostActiveStock(startTime, endTime));
  }

  @Test
  public void mostActiveStockInWindow_singleStock() {
    // Mock known symbol: NVDA
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA"));

    // Mock trade history for NVDA with one trade in the window
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    Instant startTime = TEST_TIME.minus(1, ChronoUnit.DAYS);
    Instant endTime = TEST_TIME.plus(1, ChronoUnit.DAYS);

    // Assert that NVDA is the most active stock
    assertEquals("NVDA", basicAnalyticsComputor.mostActiveStock(startTime, endTime));
  }

  @Test
  public void mostActiveStockInWindow_multipleStocks() {
    // Mock known symbols: NVDA and TSLA
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA", "TSLA"));

    // Mock trade history for NVDA
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // Mock trade history for TSLA with higher volume
    when(dataStore.getHistory("TSLA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "TSLA", 305.00, TEST_TIME.plus(10, ChronoUnit.SECONDS).toEpochMilli(), 300)));

    Instant startTime = TEST_TIME.minus(1, ChronoUnit.DAYS);
    Instant endTime = TEST_TIME.plus(1, ChronoUnit.DAYS);

    // Assert that TSLA is the most active stock (300 volume vs 100 for NVDA)
    assertEquals("TSLA", basicAnalyticsComputor.mostActiveStock(startTime, endTime));
  }

  @Test
  public void mostActiveStockInWindow_noTradesInWindow() {
    // Mock known symbols: NVDA
    when(dataStore.knownSymbols()).thenReturn(ImmutableSet.of("NVDA"));

    // Mock trade history for NVDA with trades outside the window
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 134.12, TEST_TIME.minus(10, ChronoUnit.DAYS).toEpochMilli(), 100),
                new FinhubResponse(
                    "NVDA", 134.12, TEST_TIME.plus(10, ChronoUnit.DAYS).toEpochMilli(), 200)));

    Instant startTime = TEST_TIME.minus(1, ChronoUnit.DAYS);
    Instant endTime = TEST_TIME.plus(1, ChronoUnit.DAYS);

    // Assert that no stock is active in the time window
    assertNull(basicAnalyticsComputor.mostActiveStock(startTime, endTime));
  }
}
