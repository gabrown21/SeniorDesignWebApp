package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

/** Handler for users to subscribe to a ticker symbol */
public class SubscribeHandler implements HttpHandler {
  private final StockUpdatesClient stockUpdatesClient;
  private final List<String> subscribedSymbols = new ArrayList<>();
  private static final int MAX_SUBSCRIPTIONS = 10;

  public SubscribeHandler(StockUpdatesClient stockUpdatesClient) {
    this.stockUpdatesClient = stockUpdatesClient;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];
    if (!validSymbol(symbol)) {
      handleInvalidSymbol(exchange, symbol);
      return;
    }
    if (subscribedSymbols.contains(symbol)) {
      handleAlreadyRegistered(exchange, symbol);
      return;
    }

    if (subscribedSymbols.size() >= MAX_SUBSCRIPTIONS) {
      handleSubscriptionLimit(exchange, symbol);
      return;
    }
    subscribedSymbols.add(symbol);
    stockUpdatesClient.addSymbol(symbol);
    handleSuccessfulSubscription(exchange, symbol);
  }

  private void handleInvalidSymbol(HttpExchange exchange, String symbol) throws IOException {
    Logger.info("The symbol is not valid " + symbol);
    String response = "This is a invalid symbol: " + symbol;
    sendResponse(exchange, response, 400);
  }

  private void handleAlreadyRegistered(HttpExchange exchange, String symbol) throws IOException {
    Logger.info("The symbol is already registered: " + symbol);
    String response = symbol + " has been registered already.";
    sendResponse(exchange, response, 409);
  }

  private void handleSubscriptionLimit(HttpExchange exchange, String symbol) throws IOException {
    Logger.info("Subscription limit reached for symbol: " + symbol);
    String response =
        symbol + " cannot be subscribed to because the server is at 10 subscriptions which is the limit.";
    sendResponse(exchange, response, 409);
  }

  private void handleSuccessfulSubscription(HttpExchange exchange, String symbol)
      throws IOException {
    Logger.info("Handled subscribe request for " + symbol);
    String response = "StockApp is now subscribed to updates for " + symbol + ".";
    sendResponse(exchange, response, 200);
  }

  private void sendResponse(HttpExchange exchange, String response, int responseCode)
      throws IOException {
    exchange.sendResponseHeaders(responseCode, response.length());
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }

  /**
   * Helper function to ensure that the symbol passed in is valid
   *
   * @param symbol
   * @return true if symbol is valid
   */
  public boolean validSymbol(String symbol) {
    if (symbol == null) {
      return false;
    }

    if (symbol.isEmpty() || symbol.length() > 5) {
      return false;
    }

    for (char ch : symbol.toCharArray()) {
      if (!Character.isLetterOrDigit(ch)) {
        return false;
      }
    }
    return true;
  }
}
