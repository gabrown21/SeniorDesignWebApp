package edu.bu.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.finhub.StockUpdatesClient;
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
  final StockUpdatesClient stockUpdatesClient;
  private final Map<String, Boolean> subscribedSymbols;

  public BasicWebServer(
      DataStore store, AnalyticsComputor analyticsComputor, StockUpdatesClient stockUpdatesClient) {
    this.store = store;
    this.analyticsComputor = analyticsComputor;
    this.stockUpdatesClient = stockUpdatesClient;
    this.subscribedSymbols = new HashMap<>();
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    // Create handler for price requests for individual symbols
    server.createContext("/price", new PriceHandler(analyticsComputor));

    // Create handler for listing of all known symbols
    server.createContext("/symbols", new SymbolListHandler(analyticsComputor));

    // Create handler for most active stock api
    server.createContext("/mostactive", new MostActiveStockHandler(analyticsComputor));

    server.createContext("/averagevolume", new AverageVolumePerSecondHandler(analyticsComputor));

    server.createContext("/subscribe", new SubscribeHandler(stockUpdatesClient, subscribedSymbols));

    server.createContext("/subscribed-symbols", new SubscribedSymbolsHandler(subscribedSymbols));
    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8000");
  }
}
