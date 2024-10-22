package edu.bu.queue;

import com.sun.net.httpserver.HttpServer;
import edu.bu.handlers.DequeueHandler;
import edu.bu.handlers.EnqueueHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

/** This class sets up and runs the HTTP server and manages enqueue and dequeue endpoints */
public class QueueService {

  public static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

  public void enqueueMessage(String message) {
    queue.add(message);
  }

  public String dequeueMessage() {
    return queue.poll();
  }

  public static void main(String[] args) {
    try {
      QueueService queueService = new QueueService();
      HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);
      server.createContext("/enqueue", new EnqueueHandler(queueService));
      server.createContext("/dequeue", new DequeueHandler(queueService));
      server.start();
      System.out.println("QueueService started on port 8010");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
