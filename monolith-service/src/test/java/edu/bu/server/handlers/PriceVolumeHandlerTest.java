package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.metrics.MetricsTracker;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceVolumeHandlerTest {
  private MetricsTracker metricsTracker;
  private PriceVolumeHandler priceVolumeHandler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    metricsTracker = mock(MetricsTracker.class);
    priceVolumeHandler = new PriceVolumeHandler(metricsTracker);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testHandle() throws Exception {
    String expectedResponse = "Price-volume:\nAAPL : 3\nTSLA : 2";
    when(metricsTracker.getPriceVolume()).thenReturn(expectedResponse);
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    priceVolumeHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    verify(metricsTracker).getPriceVolume();
  }
}
