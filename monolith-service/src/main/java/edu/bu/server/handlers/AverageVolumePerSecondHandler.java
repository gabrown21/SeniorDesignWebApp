package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.utilities.SymbolValidator;
import java.io.IOException;
import java.io.OutputStream;
import org.json.simple.JSONObject;
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
    JSONObject responseJson = new JSONObject();
    if (!validateSymbol(symbol, exchange)) {
      return;
    }

    try {
      double averageVolume = analyticsComputor.averageVolumePerSecond(symbol);
      responseJson.put("symbol", symbol);
      responseJson.put("averageVolumePerSecond", averageVolume);
    } catch (UnknownSymbolException e) {
      responseJson.put("error", e.getMessage());
    }
    String response = responseJson.toJSONString();
    Logger.info("Handled average volume for {}, response: {}.", symbol, response);

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, response.length());

    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(response.getBytes());
    }
  }

  private boolean validateSymbol(String symbol, HttpExchange exchange) throws IOException {
    if (!SymbolValidator.validSymbol(symbol)) {
      JSONObject responseJson = new JSONObject();
      Logger.info("The symbol is not valid:" + symbol);
      responseJson.put("error", "Invalid symbol: " + symbol);
      String response = responseJson.toJSONString();
      exchange.getResponseHeaders().add("Content-Type", "application/json");
      exchange.sendResponseHeaders(400, response.length());

      try (OutputStream outputStream = exchange.getResponseBody()) {
        outputStream.write(response.getBytes());
      }
      return false;
    }
    return true;
  }
}
