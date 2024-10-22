package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.QueueService;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** This class processes PUT requests for enqueueing messages */
public class EnqueueHandler implements HttpHandler {
  private QueueService queueService;

  public EnqueueHandler(QueueService queueService) {
    this.queueService = queueService;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
      String requestBody =
          new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

      JSONParser parser = new JSONParser();
      try {
        JSONObject jsonObject = (JSONObject) parser.parse(requestBody);
        String formattedRequestBody = jsonObject.toJSONString();
        queueService.enqueueMessage(formattedRequestBody);
        String successMessage = "Message enqueued";
        exchange.sendResponseHeaders(200, successMessage.getBytes().length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
          outputStream.write(successMessage.getBytes());
        }
      } catch (ParseException e) {
        exchange.sendResponseHeaders(400, -1);
      }
    } else {
      exchange.sendResponseHeaders(405, -1);
    }
  }
}
