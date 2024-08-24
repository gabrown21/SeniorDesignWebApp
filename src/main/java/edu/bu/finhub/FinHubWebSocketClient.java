package edu.bu.finhub;

import edu.bu.data.DataStore;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;

/**
 * Handles all interactions with the FinHub server. These include registration of stock symbols of
 * interest and handlers for FinHub responses.
 */
public class FinHubWebSocketClient extends WebSocketClient implements StockUpdatesClient {
  final DataStore store;
  final FinhubParser parser;

  public FinHubWebSocketClient(String serverUri, DataStore store) throws URISyntaxException {
    super(new URI(serverUri));

    this.store = store;
    this.parser = new FinhubParser();
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Logger.info("Registering for AAPL and TSLA");
    send("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}");
    send("{\"type\":\"subscribe\",\"symbol\":\"TSLA\"}");
  }

  @Override
  public void onMessage(String message) {
    Logger.info("Received FinHub message {}", message);
    List<FinhubResponse> response = parser.parse(message);

    store.update(response);
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
}
