package edu.bu;

import edu.bu.handlers.EnqueueingFinhubResponseHandler;
import edu.bu.persistence.StoredSymbol;
import edu.bu.persistence.SymbolsPersistence;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;

/**
 * Handles all interactions with the FinHub server. These include registration of stock symbols of
 * interest and handlers for FinHub responses.
 */
public class FinHubWebSocketClient extends WebSocketClient implements StockUpdatesClient {
  private final EnqueueingFinhubResponseHandler enqueueHandler;
  private final SymbolsPersistence symbolsPersistence;
  final Set<String> subscribedSymbols;

  public FinHubWebSocketClient(
      String serverUri,
      EnqueueingFinhubResponseHandler enqueueHandler,
      SymbolsPersistence symbolsPersistence)
      throws URISyntaxException {
    super(new URI(serverUri));
    this.enqueueHandler = enqueueHandler;
    this.symbolsPersistence = symbolsPersistence;
    this.subscribedSymbols = new ConcurrentSkipListSet<>();
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Logger.info("WebSocket connection opened, waiting for subscriptions.");
  }

  @Override
  public void onMessage(String message) {
    Logger.info("Received FinHub message {}", message);
    try {
      enqueueHandler.enqueue(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
  public void init() {
    Logger.info("Starting WebSocket based FinHub client");
    try {
      super.connectBlocking();
      this.subscribedSymbols.addAll(filterRecentSymbols(symbolsPersistence.readAll()));
      for (String symbols : subscribedSymbols) {
        String message = "{\"type\":\"subscribe\",\"symbol\":\"" + symbols + "\"}";
        send(message);
        Logger.info("Re Subscribed to symbol: {}", symbols);
      }
    } catch (InterruptedException e) {
      Logger.error("Connection interrupted : {}", e.getMessage());
    }
  }

  @Override
  public void addSymbol(String symbol) {
    Logger.info("Subscribing to updates for symbol: {}", symbol);
    subscribedSymbols.add(symbol);
    symbolsPersistence.add(symbol);
    String message = "{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public void removeSymbol(String symbol) {
    Logger.info("Unsubscribing from updates for symbol: {}", symbol);
    subscribedSymbols.remove(symbol);
    symbolsPersistence.remove(symbol);
    String message = "{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public Set<String> subscribedSymbols() {
    return Collections.unmodifiableSet(subscribedSymbols);
  }

  public Set<String> filterRecentSymbols(Set<StoredSymbol> symbols) {
    Instant now = Instant.now();
    return symbols.stream()
        .filter(storedSymbol -> Duration.between(storedSymbol.createdAt, now).toDays() <= 10)
        .map(storedSymbol -> storedSymbol.symbol)
        .collect(Collectors.toSet());
  }
}
