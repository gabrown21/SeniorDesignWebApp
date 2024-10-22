package edu.bu.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.queue.QueueService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** This class tests DequeueHandler */
public class DequeueHandlerTest {
  private DequeueHandler dequeueHandler;
  private HttpExchange exchange;
  private OutputStream outputStream;

  @BeforeEach
  void setUp() {
    dequeueHandler = new DequeueHandler(new QueueService());
    QueueService.queue = new ConcurrentLinkedQueue<>();
    exchange = mock(HttpExchange.class);
    outputStream = mock(OutputStream.class);
  }

  @Test
  void testDequeueHandlerSuccess() throws IOException {
    QueueService.queue.add("Test Message");
    when(exchange.getRequestMethod()).thenReturn("GET");
    when(exchange.getResponseBody()).thenReturn(outputStream);

    dequeueHandler.handle(exchange);

    verify(exchange).sendResponseHeaders(200, "Test Message".getBytes().length);
    verify(outputStream).write("Test Message".getBytes());
    assertTrue(QueueService.queue.isEmpty());
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
