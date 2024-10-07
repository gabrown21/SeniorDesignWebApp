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

class UnsubscribeHandlerTest {
  private Map<String, Boolean> subscribedSymbols;
  private StockUpdatesClient stockUpdatesClient;
  private UnsubscribeHandler unsubscribeHandler;
  private HttpExchange exchange;

  @BeforeEach
  void setUp() {
    subscribedSymbols = new HashMap<>();
    stockUpdatesClient = mock(StockUpdatesClient.class);
    unsubscribeHandler = new UnsubscribeHandler(stockUpdatesClient, subscribedSymbols);
    exchange = mock(HttpExchange.class);
  }

  @Test
  void testInvalidSymbolUnsubscription() throws Exception {
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/unsubscribe/INVALID@"));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
    unsubscribeHandler.handle(exchange);

    String expectedResponse = "This is a invalid symbol: INVALID@";
    verify(exchange).sendResponseHeaders(400, expectedResponse.length());
    verify(stockUpdatesClient, never()).removeSymbol(anyString());
  }

  @Test
  void testSymbolNotSubscribed() throws Exception {
    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/unsubscribe/AAPL"));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
    unsubscribeHandler.handle(exchange);

    String expectedResponse = "AAPL has not been subscribed to.";
    verify(exchange).sendResponseHeaders(409, expectedResponse.length());
    verify(stockUpdatesClient, never()).removeSymbol("AAPL");
  }

  @Test
  void testUnsubscribeMultipleSymbols() throws Exception {
    subscribedSymbols.put("AAPL", true);
    subscribedSymbols.put("TSLA", true);

    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/unsubscribe/AAPL"));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
    unsubscribeHandler.handle(exchange);
    assertFalse(subscribedSymbols.containsKey("AAPL"));
    verify(stockUpdatesClient).removeSymbol("AAPL");

    String expectedResponseAAPL = "You are now unsubscribed from updates for AAPL.";
    verify(exchange).sendResponseHeaders(200, expectedResponseAAPL.length());

    reset(exchange);

    when(exchange.getRequestURI()).thenReturn(new java.net.URI("/unsubscribe/TSLA"));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

    unsubscribeHandler.handle(exchange);

    assertFalse(subscribedSymbols.containsKey("TSLA"));
    verify(stockUpdatesClient).removeSymbol("TSLA");

    String expectedResponseTSLA = "You are now unsubscribed from updates for TSLA.";
    verify(exchange).sendResponseHeaders(200, expectedResponseTSLA.length());
  }
}
