package edu.bu.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.metrics.MetricsTracker;
import edu.bu.server.handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.tinylog.Logger;

/**
 * Registers RESTful HTTP endpoints that are supported by the StockApp. These endpoints will be
 * invoked by StockApp users to retrieve analytics computed by StockApp.
 */
public class BasicWebServer {
  final DataStore store;
  final AnalyticsComputor analyticsComputor;
  final MetricsTracker metricsTracker;
  private final Map<String, Boolean> subscribedSymbols;

  public BasicWebServer(
      DataStore store, AnalyticsComputor analyticsComputor, MetricsTracker metricsTracker) {
    this.store = store;
    this.analyticsComputor = analyticsComputor;
    this.subscribedSymbols = new HashMap<>();
    this.metricsTracker = metricsTracker;
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    // Create handler for price requests for individual symbols
    server.createContext(
        "/price", new CORSHandler(new PriceHandler(analyticsComputor, metricsTracker)));

    server.createContext("/symbols", new CORSHandler(new SymbolListHandler(analyticsComputor)));
    // Create handler for most active stock api
    server.createContext(
        "/mostactive", new CORSHandler(new MostActiveStockHandler(analyticsComputor)));

    server.createContext(
        "/averagevolume", new CORSHandler(new AverageVolumePerSecondHandler(analyticsComputor)));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8000");
  }
}
