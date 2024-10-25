package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.StockAppQueue;
import java.io.IOException;
import java.io.OutputStream;
import org.tinylog.Logger;

/** This class processes GET requests for dequeueing messages */
public class DequeueHandler implements HttpHandler {
  private StockAppQueue stockAppQueue;

  public DequeueHandler(StockAppQueue stockAppQueue) {
    this.stockAppQueue = stockAppQueue;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      String message = stockAppQueue.dequeue();

      if (message != null) {
        exchange.sendResponseHeaders(200, message.getBytes().length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
          outputStream.write(message.getBytes());
        }
        Logger.info("Message dequeued: " + message);
      } else {
        exchange.sendResponseHeaders(204, -1);
        Logger.info("Dequeue request: No message available in queue");
      }
    } else {
      exchange.sendResponseHeaders(405, -1);
      Logger.warn("Dequeue request: Invalid");
    }
  }
}
