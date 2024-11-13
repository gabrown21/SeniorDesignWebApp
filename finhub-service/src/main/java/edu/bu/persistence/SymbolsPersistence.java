package edu.bu.persistence;

import java.util.Set;

/** Symbols Persistence interface for add remove and readall */
public interface SymbolsPersistence {
  void add(String symbol);

  void remove(String symbol);

  Set<StoredSymbol> readAll();
}
