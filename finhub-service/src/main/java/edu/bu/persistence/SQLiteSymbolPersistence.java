package edu.bu.persistence;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/** SQLite symbols Persitence implementation from symmbolsPersistence */
public class SQLiteSymbolPersistence implements SymbolsPersistence {
  private static final String CONNECTION = "jdbc:sqlite:symbolpersistence.db";

  public SQLiteSymbolPersistence() {
    try (Connection conn = DriverManager.getConnection(CONNECTION)) {
      try (Statement stmt = conn.createStatement()) {
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS subscribed_symbols ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "symbol TEXT NOT NULL, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed initializing database schema", e);
    }
  }

  @Override
  public void add(String symbol) {
    String sql = "INSERT INTO subscribed_symbols (symbol) VALUES (?)";
    try (Connection conn = DriverManager.getConnection(CONNECTION);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, symbol);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed adding symbol: " + symbol, e);
    }
  }

  @Override
  public void remove(String symbol) {
    String sql = "DELETE FROM subscribed_symbols WHERE symbol = ?";
    try (Connection conn = DriverManager.getConnection(CONNECTION);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, symbol);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed removing symbol: " + symbol, e);
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    String sql = "SELECT symbol, created_at FROM subscribed_symbols";
    Set<StoredSymbol> symbols = new HashSet<>();
    try (Connection conn = DriverManager.getConnection(CONNECTION);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet ruleSet = pstmt.executeQuery()) {
      while (ruleSet.next()) {
        String symbol = ruleSet.getString("symbol");
        Instant createdAt = ruleSet.getTimestamp("created_at").toInstant();
        symbols.add(new StoredSymbol(symbol, createdAt));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed reading symbols from sql DB", e);
    }

    return symbols;
  }
}
