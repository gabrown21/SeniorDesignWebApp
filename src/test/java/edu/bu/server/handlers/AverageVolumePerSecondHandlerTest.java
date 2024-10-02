package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;

import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.BasicAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AverageVolumePerSecondHandlerTest {

  AnalyticsComputor analyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    analyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void testValidSymbol() {
    AverageVolumePerSecondHandler handler = new AverageVolumePerSecondHandler(analyticsComputor);
    assertTrue(handler.validSymbol("TSLA"));
    assertTrue(handler.validSymbol("12345"));
    assertFalse(handler.validSymbol("A@PL"));
    assertFalse(handler.validSymbol("AP-PL"));
  }
}
