package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** Handler for price updates arriving via WebSocket callbacks from Finhub. */
public class PriceHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final MetricsTracker metricsTracker;

  public PriceHandler(AnalyticsComputor analyticsComputor, MetricsTracker metricsTracker) {
    this.analyticsComputor = analyticsComputor;
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    metricsTracker.recordPriceRequest(symbol);

    String response;
    try {
      response = analyticsComputor.currentPrice(symbol) + "\n";
    } catch (UnknownSymbolException e) {
      response = e.getMessage() + "\n";
    }

    Logger.info("Handled price request for {}, responding with {}.", symbol, response);

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
