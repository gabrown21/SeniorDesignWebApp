package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.QueueService;
import java.io.IOException;
import java.io.OutputStream;

/** This class processes GET requests for dequeueing messages */
public class DequeueHandler implements HttpHandler {
  private QueueService queueService;

  public DequeueHandler(QueueService queueService) {
    this.queueService = queueService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      String message = queueService.dequeueMessage();

      if (message != null) {
        exchange.sendResponseHeaders(200, message.getBytes().length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
          outputStream.write(message.getBytes());
        }
      } else {
        exchange.sendResponseHeaders(204, -1);
      }
    } else {
      exchange.sendResponseHeaders(405, -1);
    }
  }
}
