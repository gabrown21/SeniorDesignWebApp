package edu.bu.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.metrics.MetricsTracker;
import edu.bu.server.handlers.PriceVolumeHandler;
import edu.bu.server.handlers.UpdatesVolumeHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/**
 * * Registers RESTful HTTP endpoints that are supported by the StockApp. These endpoints will be
 * invoked by StockApp users to retrieve Metrics.
 */
public class MetricsWebServer {
  private final MetricsTracker metricsTracker;

  public MetricsWebServer(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  public void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

    server.createContext("/updates-volume", new UpdatesVolumeHandler(metricsTracker));

    server.createContext("/price-volume", new PriceVolumeHandler(metricsTracker));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();
    Logger.info("MetricsWebServer is running on port 8001");
  }
}
