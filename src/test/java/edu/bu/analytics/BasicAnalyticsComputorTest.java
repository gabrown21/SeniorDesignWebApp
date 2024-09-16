package edu.bu.analytics;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
            assertThrows(UnknownSymbolException.class, () -> analyticsComputor.totalObservedVolume("NVDA"));
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

    // second response 5 seconds later
    dataStore.update(
            ImmutableList.of(new FinhubResponse(
                            "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 13 seconds later
    dataStore.update(
            ImmutableList.of(new FinhubResponse(
                            "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    //100+100+100 = 300
    assertEquals(300, analyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void mostActiveStock_noData() {
    //Should just be null for noData
    assertNull(analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    dataStore.update(
            ImmutableList.of(new FinhubResponse
                    ("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));
    //Should be just NVDA so must be most active
    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    dataStore.update(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(),100)));
    dataStore.update(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(),100)));
    dataStore.update(
            ImmutableList.of(new FinhubResponse("TSLA", 300, TEST_TIME.toEpochMilli(),300)));

    assertEquals("TSLA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    // TODO: implement
  }

  @Test
  public void knownSymbols_empty() {
    // TODO: implement
  }

  @Test
  public void knownSymbols_single() {
    // TODO: implement
  }

  @Test
  public void knownSymbols_multiple() {
    // TODO: implement
  }
}
