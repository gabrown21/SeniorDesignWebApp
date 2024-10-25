package edu.bu.queue;

import com.sun.net.httpserver.HttpServer;
import edu.bu.handlers.DequeueHandler;
import edu.bu.handlers.EnqueueHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

/** This class sets up and runs the HTTP server and manages enqueue and dequeue endpoints */
public class QueueService {

  private final StockAppQueue stockAppQueue;

  public QueueService(StockAppQueue stockAppQueue) {
    this.stockAppQueue = stockAppQueue;
  }

  public static void main(String[] args) {
    try {
      StockAppQueue stockAppQueue = new StockAppQueue();
      HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);
      server.createContext("/enqueue", new EnqueueHandler(stockAppQueue));
      server.createContext("/dequeue", new DequeueHandler(stockAppQueue));
      server.start();
      System.out.println("QueueService started on port 8010");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
