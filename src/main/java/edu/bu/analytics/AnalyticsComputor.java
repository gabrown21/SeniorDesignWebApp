package edu.bu.analytics;

import java.time.Instant;
import java.util.Set;

/** Implementors collaborate with the DataStore to support all StockApp computations. */
public interface AnalyticsComputor {
  /**
   * @return set of all stock symbols for which we have data.
   */
  Set<String> knownSymbols();

  /**
   * @param symbol stock symbol of interest
   * @return the total trade volume of the stock of interest that has been observed by the data
   *     store
   * @throws UnknownSymbolException if the requested stock symbol has not been seen by the data
   *     store
   */
  long totalObservedVolume(String symbol) throws UnknownSymbolException;

  /**
   * @return of all the stocks that the data store is aware of, the symbol of the stock that has
   *     recorded the highest total trade volume over all time.
   */
  String mostActiveStock();

  /**
   * @param symbol stock symbol of interest
   * @return the most recently reported price for the stock
   * @throws UnknownSymbolException if the requested stock symbol has not been seen by the data
   *     store
   */
  public double currentPrice(String symbol) throws UnknownSymbolException;

  /**
   * @param startTime inclusive start of the window of interest
   * @param endTime inclusive end of the window of interest
   * @return of all the stocks that the data store is aware of, the symbol of the stock that has
   *     recorded the highest total trade volume between startTime and endTime, inclusive.
   */
  public String mostActiveStock(Instant startTime, Instant endTime);

  /**
   * Calculates average colume per second of given symbol
   *
   * @param symbol
   * @return string of average volume per second of given symbol
   */
  public String averageVolumePerSecond(String symbol) throws UnknownSymbolException;
}
