package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.StockAppQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.tinylog.Logger;

/** This class processes PUT requests for enqueueing messages */
public class EnqueueHandler implements HttpHandler {
  private StockAppQueue stockAppQueue;

  public EnqueueHandler(StockAppQueue stockAppQueue) {
    this.stockAppQueue = stockAppQueue;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
      String requestBody =
          new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      stockAppQueue.enqueue(requestBody);
      Logger.info("Received and enqueued message: " + requestBody);
      String successMessage = "Message enqueued";
      exchange.sendResponseHeaders(200, successMessage.getBytes().length);
      try (OutputStream outputStream = exchange.getResponseBody()) {
        outputStream.write(successMessage.getBytes());
      }
    } else {
      exchange.sendResponseHeaders(405, -1);
    }
  }
}
