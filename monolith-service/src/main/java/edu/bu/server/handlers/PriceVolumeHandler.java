package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is meant to handle requests and track the number of times a particular stock price has
 * been requested by users
 */
public class PriceVolumeHandler implements HttpHandler {
  private final MetricsTracker metricsTracker;

  public PriceVolumeHandler(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response = metricsTracker.getPriceVolume();
    exchange.sendResponseHeaders(200, response.length());
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }
}
