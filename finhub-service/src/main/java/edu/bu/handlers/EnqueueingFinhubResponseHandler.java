package edu.bu.handlers;

import edu.bu.finhub.FinhubParser;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.tinylog.Logger;

/** Handler to handle enqueing finnhub responses */
public class EnqueueingFinhubResponseHandler {
  private final String queueServiceUrl;
  private final FinhubParser parser = new FinhubParser();

  public EnqueueingFinhubResponseHandler(String queueServiceUrl) {
    this.queueServiceUrl = queueServiceUrl;
  }

  public void enqueue(String finnhubResponse) throws IOException {
    Logger.info("Sending message to queue-service: " + finnhubResponse);
    URL url = new URL(queueServiceUrl + "/enqueue");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("PUT");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);
    byte[] input = finnhubResponse.getBytes(StandardCharsets.UTF_8);
    try (OutputStream outputStream = connection.getOutputStream()) {
      outputStream.write(input, 0, input.length);
    }
    int responseCode = connection.getResponseCode();
    Logger.info("Sent message to queue-service. Response Code: " + responseCode);
    if (responseCode != HttpURLConnection.HTTP_OK) {
      Logger.warn("Failed to enqueue message. Response Code: " + responseCode);
    }
  }

  //  public JSONObject convertStringToJson(String responseString) {
  //    Pattern pattern =
  //        Pattern.compile("symbol='(.*?)', price=(.*?), msSinceEpoch=(.*?), volume=(.*?)\\}");
  //    Matcher matcher = pattern.matcher(responseString);
  //    JSONObject jsonObject = new JSONObject();
  //    JSONArray dataArray = new JSONArray();
  //    if (matcher.find()) {
  //      JSONObject dataPoint = new JSONObject();
  //      dataPoint.put("s", matcher.group(1));
  //      dataPoint.put("p", Double.parseDouble(matcher.group(2)));
  //      dataPoint.put("t", java.time.Instant.parse(matcher.group(3)).toEpochMilli());
  //      dataPoint.put("v", Long.parseLong(matcher.group(4)));
  //
  //      dataArray.add(dataPoint);
  //    } else {
  //      Logger.warn("Failed to parse input string into JSON");
  //    }
  //    jsonObject.put("data", dataArray);
  //
  //    return jsonObject;
  //  }
}
