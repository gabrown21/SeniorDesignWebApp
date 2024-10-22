package edu.bu;

import edu.bu.metrics.MetricsTracker;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;

/**
 * Handles all interactions with the FinHub server. These include registration of stock symbols of
 * interest and handlers for FinHub responses.
 */
public class FinHubWebSocketClient extends WebSocketClient implements StockUpdatesClient {
  private final MetricsTracker metricsTracker;
  final Set<String> subscribedSymbols;

  public FinHubWebSocketClient(String serverUri, MetricsTracker metricsTracker)
      throws URISyntaxException {
    super(new URI(serverUri));
    this.subscribedSymbols = new ConcurrentSkipListSet<>();
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Logger.info("WebSocket connection opened, waiting for subscriptions.");
  }

  @Override
  public void onMessage(String message) {
    Logger.info("Received FinHub message {}", message);
    // deleted finhub parser stuff here for onMessage

  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    Logger.warn("Closing FinHub client due to {}", reason);
  }

  @Override
  public void onError(Exception exception) {
    Logger.warn("Received FinHub error: {}", exception.getMessage());
  }

  @Override
  public void connect() {
    Logger.info("Starting WebSocket based FinHub client");
    super.connect();
  }

  @Override
  public void addSymbol(String symbol) {
    Logger.info("Subscribing to updates for symbol: {}", symbol);
    subscribedSymbols.add(symbol);
    String message = "{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public void removeSymbol(String symbol) {
    Logger.info("Unsubscribing from updates for symbol: {}", symbol);
    subscribedSymbols.remove(symbol);
    String message = "{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public Set<String> subscribedSymbols() {
    return Collections.unmodifiableSet(subscribedSymbols);
  }
}
