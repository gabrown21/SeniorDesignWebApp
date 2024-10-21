package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.BasicAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.utilities.SymbolValidator;
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
    assertTrue(SymbolValidator.validSymbol("TSLA"));
    assertTrue(SymbolValidator.validSymbol("12345"));
    assertFalse(SymbolValidator.validSymbol("A@PL"));
    assertFalse(SymbolValidator.validSymbol("AP-PL"));
  }
}
