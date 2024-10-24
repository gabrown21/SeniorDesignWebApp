package edu.bu.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/** This class is meant to handle enqueue and dequeue of the poll so its encapsulated */
public class StockAppQueue {
  private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

  public void enqueue(String message) {
    queue.add(message);
  }

  public String dequeue() {
    return queue.poll();
  }
}
