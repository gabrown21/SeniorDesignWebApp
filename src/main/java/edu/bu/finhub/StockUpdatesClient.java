package edu.bu.finhub;

/**
 * Implementors provide stock updates, usually by communicating with an external data provider like
 * FinHub or by providing canned responses for testing purposes.
 */
public interface StockUpdatesClient {
  /** Connect to the server that will be providing this client with updates. */
  void connect();

  public void addSymbol(String symbol);
}
