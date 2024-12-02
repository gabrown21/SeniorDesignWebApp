package edu.bu;

import java.io.IOException;

/** Interface for finnhubresponse handlers */
public interface FinnhubResponseHandler {
  void enqueue(String response) throws IOException;
}
