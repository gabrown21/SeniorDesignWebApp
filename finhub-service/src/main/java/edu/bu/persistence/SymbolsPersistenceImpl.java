package edu.bu.persistence;

import java.util.Collections;
import java.util.Set;

/** NoOp Implenetation of SYmbolsPersistence */
public class SymbolsPersistenceImpl implements SymbolsPersistence {
  @Override
  public void add(String symbol) {}

  @Override
  public void remove(String symbol) {}

  @Override
  public Set<StoredSymbol> readAll() {
    return Collections.emptySet();
  }
}
