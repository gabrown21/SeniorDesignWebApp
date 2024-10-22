package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.StockUpdatesClient;
import java.io.IOException;
import java.io.OutputStream;

/** Handler for users to see what tickers they are subscribed to */
public class SubscribedSymbolsHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;

  public SubscribedSymbolsHandler(StockUpdatesClient stockUpdatesClient) {
    this.stockUpdatesClient = stockUpdatesClient;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response;

    if (stockUpdatesClient.subscribedSymbols().isEmpty()) {
      response = "Subscribed to no symbols.";
    } else {
      String symbolsList = String.join(", ", stockUpdatesClient.subscribedSymbols());
      response =
          "StockApp is subscribed to "
              + stockUpdatesClient.subscribedSymbols().size()
              + " symbols. Those symbols include: "
              + symbolsList;
    }

    exchange.sendResponseHeaders(200, response.length());
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }
}
