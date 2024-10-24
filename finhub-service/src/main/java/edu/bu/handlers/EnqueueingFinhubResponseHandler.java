package edu.bu.handlers;

import edu.bu.finhub.FinhubParser;
import edu.bu.finhub.FinhubResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.tinylog.Logger;

/** Handler to handle enqueing finnhub responses */
public class EnqueueingFinhubResponseHandler {
  private final String queueServiceUrl;
  private final FinhubParser parser = new FinhubParser();

  public EnqueueingFinhubResponseHandler(String queueServiceUrl) {
    this.queueServiceUrl = queueServiceUrl;
  }

  public void enqueue(String finnhubResponse) throws IOException {
    List<FinhubResponse> parsedResponses = parser.parse(finnhubResponse);
    String jsonString = buildJsonString(parsedResponses);
    Logger.info("Sending message to queue-service: " + jsonString);
    URL url = new URL(queueServiceUrl + "/enqueue");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("PUT");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);
    byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
    try (OutputStream outputStream = connection.getOutputStream()) {
      outputStream.write(input, 0, input.length);
    }
    int responseCode = connection.getResponseCode();
    Logger.info("Sent message to queue-service. Response Code: " + responseCode);
    if (responseCode != HttpURLConnection.HTTP_OK) {
      Logger.warn("Failed to enqueue message. Response Code: " + responseCode);
    }
  }

  private String buildJsonString(List<FinhubResponse> responses) {
    JSONObject json = new JSONObject();
    JSONArray dataArray = new JSONArray();

    for (FinhubResponse response : responses) {
      JSONObject dataPoint = new JSONObject();
      dataPoint.put("s", response.symbol);
      dataPoint.put("p", response.price);
      dataPoint.put("t", response.msSinceEpoch);
      dataPoint.put("v", response.volume);
      dataArray.add(dataPoint);
    }

    json.put("data", dataArray);
    return json.toString();
  }
}
