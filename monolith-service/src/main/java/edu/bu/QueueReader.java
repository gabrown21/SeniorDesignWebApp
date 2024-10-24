package edu.bu;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubParser;
import edu.bu.finhub.FinhubResponse;
import edu.bu.metrics.MetricsTracker;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.tinylog.Logger;

/**
 * QueueReader reads messages from the queue-service and updates the DataStore with parsed
 * responses.
 */
public class QueueReader {

  private final DataStore dataStore;
  private final String queueServiceUrl;
  private final HttpClient httpClient;
  private final FinhubParser parser;
  private final MetricsTracker metricsTracker;

  public QueueReader(DataStore dataStore, String queueServiceUrl, MetricsTracker metricsTracker) {
    this.dataStore = dataStore;
    this.queueServiceUrl = queueServiceUrl;
    this.metricsTracker = metricsTracker;
    this.httpClient = HttpClient.newHttpClient();
    this.parser = new FinhubParser();
  }

  public void start() {
    new Thread(this::pollQueue).start();
  }

  private void pollQueue() {
    while (true) {
      try {
        HttpResponse<String> response = sendDequeueRequest();
        processQueueResponse(response);

        Thread.sleep(3000);
      } catch (InterruptedException | IOException e) {
        Logger.error("QueueReader encountered an error: " + e.getMessage());
        break;
      } catch (Exception e) {
        Logger.error("Unexpected error: " + e.getMessage());
      }
    }
  }

  private HttpResponse<String> sendDequeueRequest() throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create(queueServiceUrl + "/dequeue")).GET().build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private void processQueueResponse(HttpResponse<String> response) {
    if (response.statusCode() == 200) {
      String message = response.body();
      Logger.info("Received message from queue-service: " + message);
      List<FinhubResponse> parsedResponse = parser.parse(message);
      if (!parsedResponse.isEmpty()) {
        handleParsedResponse(parsedResponse);
      } else {
        Logger.warn("Failed to parse message into FinnhubResponse.");
      }
    } else if (response.statusCode() == 204) {
      Logger.info("Queue is empty, waiting before polling again...");
    } else {
      Logger.error("Unexpected response from queue-service. Status: " + response.statusCode());
    }
  }

  private void handleParsedResponse(List<FinhubResponse> parsedResponse) {
    dataStore.update(parsedResponse);
    Logger.info("DataStore updated with new responses.");
    parsedResponse.forEach(response -> metricsTracker.recordUpdate(response.symbol));
  }
}
