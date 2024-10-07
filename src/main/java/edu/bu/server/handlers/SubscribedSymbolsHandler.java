package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/** Handler for users to see what tickers they are subscribed to */
public class SubscribedSymbolsHandler implements HttpHandler {
  private final Map<String, Boolean> subscribedSymbols;

  public SubscribedSymbolsHandler(Map<String, Boolean> subscribedSymbols) {
    this.subscribedSymbols = subscribedSymbols;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response;

    if (subscribedSymbols.isEmpty()) {
      response = "Subscribed to no symbols.";
    } else {
      String symbolsList = String.join(", ", subscribedSymbols.keySet());
      response =
          "StockApp is subscribed to "
              + subscribedSymbols.size()
              + " symbols. Those symbols include: "
              + symbolsList;
    }

    exchange.sendResponseHeaders(200, response.length());
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }
}
