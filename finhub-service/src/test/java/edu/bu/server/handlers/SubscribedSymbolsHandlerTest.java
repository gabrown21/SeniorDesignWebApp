package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.StockUpdatesClient;
import edu.bu.handlers.SubscribedSymbolsHandler;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscribedSymbolsHandlerTest {

  private SubscribedSymbolsHandler subscribedSymbolsHandler;
  private HttpExchange exchange;
  private StockUpdatesClient stockUpdatesClient;

  @BeforeEach
  void setUp() {
    stockUpdatesClient = mock(StockUpdatesClient.class);
    subscribedSymbolsHandler = new SubscribedSymbolsHandler(stockUpdatesClient);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testNoSymbolsSubscribed() throws Exception {
    Set<String> subscribedSymbols = new HashSet<>();
    when(stockUpdatesClient.subscribedSymbols()).thenReturn(subscribedSymbols);
    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
    when(exchange.getResponseBody()).thenReturn(responseStream);

    subscribedSymbolsHandler.handle(exchange);

    String expectedResponse = "Subscribed to no symbols.";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    assertEquals(expectedResponse, responseStream.toString());
  }

  @Test
  void testSymbolsSubscribed() throws Exception {
    Set<String> subscribedSymbols = new HashSet<>();
    subscribedSymbols.add("AAPL");
    subscribedSymbols.add("TSLA");
    when(stockUpdatesClient.subscribedSymbols()).thenReturn(subscribedSymbols);

    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
    when(exchange.getResponseBody()).thenReturn(responseStream);

    subscribedSymbolsHandler.handle(exchange);

    String expectedResponse =
        "StockApp is subscribed to 2 symbols. Those symbols include: AAPL, TSLA";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    assertEquals(expectedResponse, responseStream.toString());
  }
}
