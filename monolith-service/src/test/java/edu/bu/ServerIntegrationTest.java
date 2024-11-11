package edu.bu;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ServerIntegrationTest {

  HttpClient httpClient;
  JSONParser parser = new JSONParser();

  @BeforeEach
  public void setUp() {
    httpClient = HttpClient.newBuilder().build();
  }

  @Test
  public void requestAgainstRunningServer_knownSymbols()
      throws URISyntaxException, IOException, InterruptedException, ParseException {

    String rawResponse = makeRequest("http://localhost:8000/symbols");
    JSONParser parser = new JSONParser();
    JSONObject parsedResponse = (JSONObject) parser.parse(rawResponse);
    JSONArray jsonArray = (JSONArray) parsedResponse.get("symbols");
    Set<String> knownSymbols = new HashSet<>();
    for (Object element : jsonArray) {
      knownSymbols.add(element.toString());
    }
    assertEquals(ImmutableSet.of("BAC", "RIVN", "NKE"), knownSymbols);
  }

  @Test
  public void requestAgainstRunningServer_price()
      throws URISyntaxException, IOException, InterruptedException, ParseException {
    String rawResponse = makeRequest("http://localhost:8000/price/BAC");
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(rawResponse);
    Object priceObj = jsonObject.get("currentPrice");
    if (priceObj == null) {
      throw new IllegalArgumentException("currentPrice key is missing in the JSON response");
    }
    double price = Double.parseDouble(priceObj.toString());
    assertEquals(43.1, price, 0.01);
  }

  @Test
  public void requestAgainstRunningServer_mostActiveStock()
      throws URISyntaxException, IOException, InterruptedException, ParseException {
    String rawResponse = makeRequest("http://localhost:8000/mostactive");
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(rawResponse);

    Object symbolObj = jsonObject.get("mostActiveStock");
    if (symbolObj == null) {
      throw new IllegalArgumentException("mostActiveStock key is missing in the JSON response");
    }
    assertEquals("RIVN", symbolObj.toString());
  }

  String makeRequest(String url) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    return response.body().strip();
  }
}
