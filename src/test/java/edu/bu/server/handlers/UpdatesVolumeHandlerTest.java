package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.metrics.MetricsTracker;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdatesVolumeHandlerTest {
  private MetricsTracker metricsTracker;
  private UpdatesVolumeHandler handler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    metricsTracker = mock(MetricsTracker.class);
    handler = new UpdatesVolumeHandler(metricsTracker);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testHandle() throws Exception {
    String expectedResponse = "Updates-volume:\nAAPL : 5\nTSLA : 3";
    when(metricsTracker.getUpdatesVolume()).thenReturn(expectedResponse);
    OutputStream outputStream = new ByteArrayOutputStream();
    when(exchange.getResponseBody()).thenReturn(outputStream);

    handler.handle(exchange);
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    assertEquals(expectedResponse, outputStream.toString());
  }
}
