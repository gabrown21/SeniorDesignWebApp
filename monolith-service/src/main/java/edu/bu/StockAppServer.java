package edu.bu;

import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.CachingAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.metrics.MetricsTracker;
import edu.bu.server.BasicWebServer;
import edu.bu.server.MetricsWebServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.tinylog.Logger;

/**
 * Entry point for the entire single process StockApp. It initializes the following collaborators:
 * <li>FinHubClient - to register listeners for WebSocket updates from FinHub
 * <li>BasicWebServer - to configure supported HTTP endpoints for our (StockApp) users
 * <li>DataStore - an instance of a data store to store data that we receive from FinHub
 * <li>BasicAnalyticsComputor - business logic around all computations performed by our app
 */
public class StockAppServer {
  static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";
  static final String QUEUE_SERVICE_URL = "http://localhost:8010/dequeue";
  static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";

  // StockAppServer
  public static void main(String[] args) throws IOException, URISyntaxException {
    Logger.info("Starting StockAppServer with arguments: {}", List.of(args));

    // set up store
    DataStore store = new InMemoryStore();
    MetricsTracker metricsTracker = new MetricsTracker();

    // set up analytics computations
    AnalyticsComputor analyticsComputor = new CachingAnalyticsComputor(store);

    // start web server
    BasicWebServer webServer = new BasicWebServer(store, analyticsComputor, metricsTracker);
    webServer.start();

    SQSQueueReader queueReader = new SQSQueueReader(store, metricsTracker);
    try {
      queueReader.start();
    } catch (Exception e) {
      Logger.error("Failed to start QueueReader: " + e.getMessage());
    }

    MetricsWebServer metricsWebServer = new MetricsWebServer(metricsTracker);
    metricsWebServer.start();
  }
}
