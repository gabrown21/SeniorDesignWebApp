package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.utilities.SymbolValidator;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** Handler for users to unsubscribe from a ticker symbol */
public class UnsubscribeHandler implements HttpHandler {
  private final StockUpdatesClient stockUpdatesClient;

  public UnsubscribeHandler(StockUpdatesClient stockUpdatesClient) {
    this.stockUpdatesClient = stockUpdatesClient;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    if (!SymbolValidator.validSymbol(symbol)) {
      handleInvalidSymbol(exchange, symbol);
      return;
    }

    if (!stockUpdatesClient.subscribedSymbols().contains(symbol)) {
      handleNotSubscribed(exchange, symbol);
      return;
    }

    stockUpdatesClient.removeSymbol(symbol);
    handleSuccessfulUnsubscription(exchange, symbol);
  }

  private void handleInvalidSymbol(HttpExchange exchange, String symbol) throws IOException {
    Logger.info("The symbol is not valid " + symbol);
    String response = "This is a invalid symbol: " + symbol;
    sendResponse(exchange, response, 400);
  }

  private void handleNotSubscribed(HttpExchange exchange, String symbol) throws IOException {
    Logger.info("The symbol is not currently subscribed: " + symbol);
    String response = symbol + " has not been subscribed to.";
    sendResponse(exchange, response, 409);
  }

  private void handleSuccessfulUnsubscription(HttpExchange exchange, String symbol)
      throws IOException {
    Logger.info("Handled request to unsubscribe for " + symbol);
    String response = "You are now unsubscribed from updates for " + symbol + ".";
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
