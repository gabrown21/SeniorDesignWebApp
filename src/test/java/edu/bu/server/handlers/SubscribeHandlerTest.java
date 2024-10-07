package edu.bu.server.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.finhub.StockUpdatesClient;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscribeHandlerTest {
  private Map<String, Boolean> subscribedSymbols;
  private StockUpdatesClient stockUpdatesClient;
  private SubscribeHandler subscribeHandler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    subscribedSymbols = new HashMap<>();
    stockUpdatesClient = mock(StockUpdatesClient.class);
    subscribeHandler = new SubscribeHandler(stockUpdatesClient, subscribedSymbols);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testValidSymbolSubscription() throws Exception {
    String symbol = "AAPL";
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + symbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);

    assertTrue(subscribedSymbols.containsKey(symbol));
    verify(stockUpdatesClient).addSymbol(symbol);
    String expectedResponse = "StockApp is now subscribed to updates for " + symbol + ".";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
  }

  @Test
  void testDuplicateSymbolSubscription() throws Exception {
    subscribedSymbols.put("AAPL", true);

    String symbol = "AAPL";
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + symbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);
    String expectedResponse = symbol + " has been registered already.";
    verify(exchange).sendResponseHeaders(409, expectedResponse.length());
    verify(stockUpdatesClient, never()).addSymbol(symbol);
  }

  @Test
  void testInvalidSymbolSubscription() throws Exception {
    String invalidSymbol = "ABC@";
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + invalidSymbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);
    String expectedResponse = "This is a invalid symbol: " + invalidSymbol;
    verify(exchange).sendResponseHeaders(400, expectedResponse.length());
    verify(stockUpdatesClient, never()).addSymbol(invalidSymbol); // Ensure addSymbol was not called
  }

  @Test
  void testSubscriptionLimitReached() throws Exception {
    for (int i = 0; i < 10; i++) {
      subscribedSymbols.put("SYM" + i, true);
    }
    String symbol = "AAPL";

    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + symbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);
    String expectedResponse =
        symbol
            + " cannot be subscribed to because the server is at 10 subscriptions which is the limit.";
    verify(exchange).sendResponseHeaders(409, expectedResponse.length());
    verify(stockUpdatesClient, never()).addSymbol(symbol);
  }
}
