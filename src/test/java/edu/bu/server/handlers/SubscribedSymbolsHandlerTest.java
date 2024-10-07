package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscribedSymbolsHandlerTest {

  private Map<String, Boolean> subscribedSymbols;
  private SubscribedSymbolsHandler subscribedSymbolsHandler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    subscribedSymbols = new HashMap<>();
    subscribedSymbolsHandler = new SubscribedSymbolsHandler(subscribedSymbols);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testNoSymbolsSubscribed() throws Exception {
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribedSymbolsHandler.handle(exchange);
    String expectedResponse = "Subscribed to no symbols.";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    assertEquals(expectedResponse, exchange.getResponseBody().toString());
  }

  @Test
  void testSymbolsSubscribed() throws Exception {
    subscribedSymbols.put("AAPL", true);
    subscribedSymbols.put("TSLA", true);

    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribedSymbolsHandler.handle(exchange);
    String expectedResponse =
        "StockApp is subscribed to 2 symbols. Those symbols include: AAPL, TSLA";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
    assertEquals(expectedResponse, exchange.getResponseBody().toString());
  }
}
