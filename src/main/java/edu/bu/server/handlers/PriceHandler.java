package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** Handler for price updates arriving via WebSocket callbacks from Finhub. */
public class PriceHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public PriceHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    String response;
    try {
      response = analyticsComputor.currentPrice(symbol) + "\n";
    } catch (UnknownSymbolException e) {
      response = e.getMessage() + "\n";
    }

    Logger.info("Handled price request for {}, responding with {}.", symbol, response);

    exchange.sendResponseHeaders(200, response.length());

    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(response.getBytes());
  }
}
