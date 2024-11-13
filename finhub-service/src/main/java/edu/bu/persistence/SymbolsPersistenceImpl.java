package edu.bu.persistence;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/** NoOp Implenetation of SYmbolsPersistence */
public class SymbolsPersistenceImpl implements SymbolsPersistence {
  private static final Path STORAGE_DIRECTORY = Paths.get("symbol-storage");

  public SymbolsPersistenceImpl() {
    try {
      if (!Files.exists(STORAGE_DIRECTORY)) {
        Files.createDirectories(STORAGE_DIRECTORY);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create symbol-storage directory", e);
    }
  }

  @Override
  public void add(String symbol) {
    Path symbolPath = STORAGE_DIRECTORY.resolve(symbol);
    try {
      if (!Files.exists(symbolPath)) {
        Files.createFile(symbolPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to store symbol: " + symbol, e);
    }
  }

  @Override
  public void remove(String symbol) {
    Path symbolPath = STORAGE_DIRECTORY.resolve(symbol);
    try {
      if (Files.exists(symbolPath)) {
        Files.delete(symbolPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to remove symbol: " + symbol, e);
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    Set<StoredSymbol> symbols = new HashSet<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(STORAGE_DIRECTORY)) {
      for (Path symbolPath : stream) {
        BasicFileAttributes attrs = Files.readAttributes(symbolPath, BasicFileAttributes.class);
        Instant createdAt = attrs.creationTime().toInstant();
        String symbol = symbolPath.getFileName().toString();
        symbols.add(new StoredSymbol(symbol, createdAt));
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read all symbols from storage", e);
    }

    return symbols;
  }
}
