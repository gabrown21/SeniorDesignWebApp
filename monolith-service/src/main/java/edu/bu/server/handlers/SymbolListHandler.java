package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import java.io.IOException;
import java.io.OutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Supports the symbols HTTP endpoint. */
public class SymbolListHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public SymbolListHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    JSONArray symbolsArray = new JSONArray();
    symbolsArray.addAll(analyticsComputor.knownSymbols());
    JSONObject responseJson = new JSONObject();
    responseJson.put("symbols", symbolsArray);

    String response = responseJson.toJSONString();

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, response.length());

    OutputStream outputStream = null;
    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
    } finally {
      outputStream.close();
    }
  }
}
