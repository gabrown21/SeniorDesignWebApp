package edu.bu;

import com.sun.net.httpserver.HttpServer;
import edu.bu.handlers.EnqueueingFinhubResponseHandler;
import edu.bu.handlers.SubscribeHandler;
import edu.bu.handlers.SubscribedSymbolsHandler;
import edu.bu.handlers.UnsubscribeHandler;
import edu.bu.persistence.SymbolsPersistenceImpl;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.tinylog.Logger;

/** Class for finhub-service and establishes the endpoints related to finhub. */
public class FinhubService {
  static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";
  static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";
  static final SymbolsPersistenceImpl symbolsPersistence = new SymbolsPersistenceImpl();

  public static void main(String[] args) throws IOException, URISyntaxException {
    Logger.info("Starting FinnhubService with arguments: {}", List.of(args));
    StockUpdatesClient stockUpdatesClient =
        List.of(args).contains(MOCK_FINHUB_ARGUMENT)
            ? new MockFinhubClient(
                new EnqueueingFinhubResponseHandler("http://localhost:8010"), Set.of(args))
            : new FinHubWebSocketClient(
                WEBHOOK_URI + "?token=" + API_TOKEN,
                new EnqueueingFinhubResponseHandler("http://localhost:8010"),
                symbolsPersistence);
    stockUpdatesClient.init();

    // Start HTTP server for handling subscribe/unsubscribe requests
    HttpServer server = HttpServer.create(new InetSocketAddress(8004), 0);
    server.createContext("/subscribe", new SubscribeHandler(stockUpdatesClient));
    server.createContext("/unsubscribe", new UnsubscribeHandler(stockUpdatesClient));
    server.createContext("/subscribed-symbols", new SubscribedSymbolsHandler(stockUpdatesClient));

    server.setExecutor(null); // Default executor
    server.start();

    Logger.info("FinnhubService started on port 8004");
  }
}
