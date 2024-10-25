package edu.bu.server.handlers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.StockUpdatesClient;
import edu.bu.handlers.SubscribeHandler;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscribeHandlerTest {
  private StockUpdatesClient stockUpdatesClient;
  private SubscribeHandler subscribeHandler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    stockUpdatesClient = mock(StockUpdatesClient.class);
    subscribeHandler = new SubscribeHandler(stockUpdatesClient);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testValidSymbolSubscription() throws Exception {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    when(stockUpdatesClient.subscribedSymbols()).thenReturn(subscribedSymbols);
    doAnswer(
            invocation -> {
              subscribedSymbols.add(symbol);
              return null;
            })
        .when(stockUpdatesClient)
        .addSymbol(symbol);
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + symbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);
    assertTrue(stockUpdatesClient.subscribedSymbols().contains(symbol));

    verify(stockUpdatesClient).addSymbol(symbol);

    String expectedResponse = "StockApp is now subscribed to updates for " + symbol + ".";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
  }

  @Test
  void testDuplicateSymbolSubscription() throws Exception {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    when(stockUpdatesClient.subscribedSymbols()).thenReturn(subscribedSymbols);
    doAnswer(
            invocation -> {
              String addedSymbol = invocation.getArgument(0);
              subscribedSymbols.add(addedSymbol);
              return null;
            })
        .when(stockUpdatesClient)
        .addSymbol(symbol);

    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/subscribe/" + symbol));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    subscribeHandler.handle(exchange);
    assertTrue(stockUpdatesClient.subscribedSymbols().contains(symbol));
    verify(stockUpdatesClient).addSymbol(symbol);

    String expectedResponse = "StockApp is now subscribed to updates for " + symbol + ".";
    verify(exchange).sendResponseHeaders(200, expectedResponse.length());
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
    Set<String> subscribedSymbols = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      subscribedSymbols.add("SYM" + i);
    }
    when(stockUpdatesClient.subscribedSymbols()).thenReturn(subscribedSymbols);

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
