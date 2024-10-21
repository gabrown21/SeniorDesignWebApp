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
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ServerIntegrationTest {

  HttpClient httpClient;

  @BeforeEach
  public void setUp() {
    httpClient = HttpClient.newBuilder().build();
  }

  @Test
  public void requestAgainstRunningServer_knownSymbols()
      throws URISyntaxException, IOException, InterruptedException {

    String rawResponse = makeRequest("http://localhost:8000/symbols");
    Set<String> knownSymbols = new HashSet<>(List.of(rawResponse.split(",")));

    assertEquals(ImmutableSet.of("BAC", "RIVN", "NKE"), knownSymbols);
  }

  @Test
  public void requestAgainstRunningServer_price()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8000/price/BAC");
    double price = Double.parseDouble(rawResponse);
    assertEquals(43.1, price, 0.01);
  }

  @Test
  public void requestAgainstRunningServer_mostActiveStock()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8000/mostactive");
    assertEquals("RIVN", rawResponse);
  }

  String makeRequest(String url) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    return response.body().strip();
  }
}
