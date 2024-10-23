package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

/** Supports the symbols HTTP endpoint. */
public class SymbolListHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;

  public SymbolListHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response =
        analyticsComputor.knownSymbols().stream().collect(Collectors.joining(",")) + "\n";

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
