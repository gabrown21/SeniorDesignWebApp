package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.utilities.SymbolValidator;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.tinylog.Logger;

/** Handler for users to subscribe to a ticker symbol */
public class SubscribeHandler implements HttpHandler {
  private final StockUpdatesClient stockUpdatesClient;
  private final Map<String, Boolean> subscribedSymbols;
  private static final int MAX_SUBSCRIPTIONS = 10;

  public SubscribeHandler(
      StockUpdatesClient stockUpdatesClient, Map<String, Boolean> subscribedSymbols) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.subscribedSymbols = subscribedSymbols;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];
    if (!SymbolValidator.validSymbol(symbol)) {
      handleInvalidSymbol(exchange, symbol);
      return;
    }
    if (subscribedSymbols.containsKey(symbol)) {
      handleAlreadyRegistered(exchange, symbol);
      return;
    }

    if (subscribedSymbols.size() >= MAX_SUBSCRIPTIONS) {
      handleSubscriptionLimit(exchange, symbol);
      return;
    }
    subscribedSymbols.put(symbol, true);
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
        symbol
            + " cannot be subscribed to because the server is at 10 subscriptions which is the limit.";
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
}
