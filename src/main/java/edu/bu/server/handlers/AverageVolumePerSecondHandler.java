package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.utilities.SymbolValidator;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** Handler for average volume per second. */
public class AverageVolumePerSecondHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public AverageVolumePerSecondHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    if (!SymbolValidator.validSymbol(symbol)) {
      Logger.info("The symbol is not valid:" + symbol);
      String response = "This is an invalid symbol: " + symbol;
      exchange.sendResponseHeaders(400, response.length());
      try (OutputStream outputStream = exchange.getResponseBody()) {
        outputStream.write(response.getBytes());
      }
      return;
    }

    String response;
    try {
      response = analyticsComputor.averageVolumePerSecond(symbol) + "\n";
    } catch (UnknownSymbolException e) {
      response = e.getMessage();
    }

    Logger.info("Handled average volume for {}, response: {}.", symbol, response);

    exchange.sendResponseHeaders(200, response.length());

    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }
}
