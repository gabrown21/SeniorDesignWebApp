package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import java.io.IOException;
import java.io.OutputStream;
import org.json.simple.JSONObject;
import org.tinylog.Logger;

/** Most active stock handler that inherits from HttpHandler */
public class MostActiveStockHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public MostActiveStockHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    JSONObject responseJson = new JSONObject();
    String mostActiveStock = analyticsComputor.mostActiveStock();
    responseJson.put("mostActiveStock", mostActiveStock);
    long totalVolume = 0;
    try {
      totalVolume = analyticsComputor.totalObservedVolume(mostActiveStock);
    } catch (UnknownSymbolException e) {
      Logger.error(e.getMessage());
    }
    String response = responseJson.toJSONString() + " : " + totalVolume;
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
