package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicAnalyticsComputorTest {

  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);

  AnalyticsComputor analyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    analyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void currentPrice_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(UnknownSymbolException.class, () -> analyticsComputor.currentPrice("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void currentPrice_singleValue() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(134.12, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void currentPrice_multipleValues() throws UnknownSymbolException {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals(135.01, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_uknownSymbol() {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> analyticsComputor.totalObservedVolume("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100, analyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    // 100+100+100 = 300
    assertEquals(300, analyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void mostActiveStock_noData() {
    // Should just be null for noData
    assertNull(analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));
    // Should be just NVDA so must be most active
    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 134.12, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 300, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 300)));
    // 300>268.24
    assertEquals("TSLA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 134.12, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 300, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 300, TEST_TIME.plus(20, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 300, TEST_TIME.plus(27, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    // TSLA volume = 300 > NVDA volume of 200 & More TSLA Data points
    assertEquals("TSLA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void knownSymbols_empty() {
    assertTrue(analyticsComputor.knownSymbols().isEmpty());
  }

  @Test
  public void knownSymbols_single() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));
    // Includes the size comparison this way as well
    assertEquals(ImmutableSet.of("NVDA"), analyticsComputor.knownSymbols());
  }

  @Test
  public void knownSymbols_multiple() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 300, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "TSLA", 290, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertTrue(analyticsComputor.knownSymbols().contains("AAPL"));
    assertTrue(analyticsComputor.knownSymbols().contains("TSLA"));
    assertTrue(analyticsComputor.knownSymbols().contains("NVDA"));
    // Added in based on PR notes which furthers makes sure those are the only three
    assertEquals(3, analyticsComputor.knownSymbols().size());
  }

  @Test
  public void averageVolumePerSecond_singleValue() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 300));

    dataStore.update(singleResponse);

    String response = analyticsComputor.averageVolumePerSecond("NVDA");
    assertEquals("300.00", response);
  }

  @Test
  public void averageVolumePerSecond_multipleValues() throws UnknownSymbolException {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 300)));

    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(400, ChronoUnit.SECONDS).toEpochMilli(), 500)));

    String response = analyticsComputor.averageVolumePerSecond("NVDA");
    assertEquals("2.00", response);
  }
}
