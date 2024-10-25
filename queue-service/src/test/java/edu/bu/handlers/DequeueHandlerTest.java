package edu.bu.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.queue.QueueService;
import edu.bu.queue.StockAppQueue;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** This class tests DequeueHandler */
public class DequeueHandlerTest {
  private DequeueHandler dequeueHandler;
  private HttpExchange exchange;
  private OutputStream outputStream;
  private StockAppQueue stockAppQueue;

  @BeforeEach
  void setUp() {
    stockAppQueue = new StockAppQueue();
    QueueService queueService = new QueueService(stockAppQueue);
    dequeueHandler = new DequeueHandler(stockAppQueue);
    exchange = mock(HttpExchange.class);
    outputStream = mock(OutputStream.class);
  }

  @Test
  void testDequeueHandlerSuccess() throws IOException {
    stockAppQueue.enqueue("Test Message");
    when(exchange.getRequestMethod()).thenReturn("GET");
    when(exchange.getResponseBody()).thenReturn(outputStream);

    dequeueHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(200, "Test Message".getBytes().length);
    verify(outputStream).write("Test Message".getBytes());
    assertTrue(stockAppQueue.dequeue() == null);
  }

  @Test
  void testDequeueHandlerNoContent() throws IOException {
    when(exchange.getRequestMethod()).thenReturn("GET");
    dequeueHandler.handle(exchange);
    verify(exchange).sendResponseHeaders(204, -1);
  }

  @Test
  void testDequeueHandlerWrongMethod() throws IOException {
    when(exchange.getRequestMethod()).thenReturn("POST");
    dequeueHandler.handle(exchange);
    verify(exchange).sendResponseHeaders(405, -1);
  }
}
