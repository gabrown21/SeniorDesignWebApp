package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachingAnalyticsComputorTest {
  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);
  CachingAnalyticsComputor cachingAnalyticsComputor;
  InMemoryStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    cachingAnalyticsComputor = new CachingAnalyticsComputor(dataStore);
  }

  @Test
  public void totalObservedVolume_singleCall() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));
    dataStore.update(singleResponse);
    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_subsequentCallsWithNoUpdateComeFromCache()
      throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));
    dataStore.update(singleResponse);
    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
    // we are going to put a fake, incorrect marker response in the cache to allow us to easily
    // verify that the cache
    // was used and not cleared since last call - it should not have been because there have been no
    // updates
    cachingAnalyticsComputor.totalObservedVolumeCache.put("NVDA", -999L);
    assertEquals(-999L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_cacheClearedOnUpdate() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));
    dataStore.update(singleResponse);
    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
    // we are going to put a fake, incorrect marker response in the cache an then send an update
    // through the data
    // store - this marker value should be dropped and caching computer should have correct result
    cachingAnalyticsComputor.totalObservedVolumeCache.put("NVDA", -999L);
    List<FinhubResponse> secondResponse =
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 134.12, TEST_TIME.plus(4, ChronoUnit.SECONDS).toEpochMilli(), 18));
    dataStore.update(secondResponse);
    assertEquals(118, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_handleNewSymbolRequest_noCacheInvalidations()
      throws UnknownSymbolException {
    List<FinhubResponse> firstResponses =
        ImmutableList.of(
            new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100),
            new FinhubResponse("TSLA", 100.00, TEST_TIME.toEpochMilli(), 300));
    dataStore.update(firstResponses);
    // assert that cache is empty
    assertFalse(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("NVDA"));
    assertFalse(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("TSLA"));
    assertEquals(100, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
    // cache should now be populated for NVDA
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("NVDA"));
    assertFalse(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("TSLA"));
    assertEquals(300, cachingAnalyticsComputor.totalObservedVolume("TSLA"));
    // cache should now be populated for NVDA and TSLA
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("NVDA"));
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("TSLA"));
    List<FinhubResponse> secondResponses =
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.12, TEST_TIME.plus(3, ChronoUnit.MINUTES).toEpochMilli(), 90),
            new FinhubResponse(
                "TSLA", 101.00, TEST_TIME.plus(3, ChronoUnit.MINUTES).toEpochMilli(), 230));
    dataStore.update(secondResponses);
    assertEquals(190, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
    // cache should now be populated for NVDA
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("NVDA"));
    assertFalse(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("TSLA"));
    assertEquals(530, cachingAnalyticsComputor.totalObservedVolume("TSLA"));
    // cache should now be populated for NVDA and TSLA
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("NVDA"));
    assertTrue(cachingAnalyticsComputor.totalObservedVolumeCache.containsKey("TSLA"));
  }
}
