package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
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

    if (!validSymbol(symbol)) {
      Logger.info("The symbol is not valid:" + symbol);
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
