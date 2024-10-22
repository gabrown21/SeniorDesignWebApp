package edu.bu.handlers;

import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.queue.QueueService;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** This class tests EnqueueHandler */
public class EnqueueHandlerTest {
  private EnqueueHandler enqueueHandler;
  private HttpExchange exchange;
  private OutputStream outputStream;

  @BeforeEach
  void setUp() {
    enqueueHandler = new EnqueueHandler(new QueueService());
    exchange = mock(HttpExchange.class);
    outputStream = mock(OutputStream.class);
  }

  @Test
  void testEnqueueHandlerWrongMethod() throws IOException {
    when(exchange.getRequestMethod()).thenReturn("GET");
    enqueueHandler.handle(exchange);
    verify(exchange).sendResponseHeaders(405, -1);
  }
}
