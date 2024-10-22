package edu.bu;

import com.sun.net.httpserver.HttpServer;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.handlers.SubscribeHandler;
import edu.bu.handlers.SubscribedSymbolsHandler;
import edu.bu.handlers.UnsubscribeHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/** Class for finhub-service and establishes the endpoints related to finhub. */
public class FinnhubService {
  final StockUpdatesClient stockUpdatesClient;
  final AnalyticsComputor analyticsComputor;

  public FinnhubService(
      StockUpdatesClient stockUpdatesClient, AnalyticsComputor analyticsComputor) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.analyticsComputor = analyticsComputor;
  }

  public void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8004), 0);
    server.createContext("/subscribe", new SubscribeHandler(stockUpdatesClient));
    server.createContext("/unsubscribe", new UnsubscribeHandler(stockUpdatesClient));
    server.createContext("/subscribed-symbols", new SubscribedSymbolsHandler(stockUpdatesClient));

    server.setExecutor(null);
    server.start();
    Logger.info("FinnhubService started on port 8004");
  }
}
