package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
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
    // TODO: implement
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    // TODO: implement
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    // TODO: implement
  }

  @Test
  public void mostActiveStock_noData() {
    // TODO: implement
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    // TODO: implement
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    // TODO: implement
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    // TODO: implement
  }

  @Test
  public void knownSymbols_single() {
    // TODO: implement
  }
}
