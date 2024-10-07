package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.io.OutputStream;

/** This class will update the volume for the metrics */
public class UpdatesVolumeHandler implements HttpHandler {
  private final MetricsTracker metricsTracker;

  public UpdatesVolumeHandler(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response = metricsTracker.getUpdatesVolume();
    exchange.sendResponseHeaders(200, response.length());
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }
}
