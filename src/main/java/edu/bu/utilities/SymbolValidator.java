package edu.bu.utilities;

/** This class is meant to be a tool to validate a ticker symbol */
public class SymbolValidator {
  /**
   * Helper function to ensure that the symbol passed in is valid
   *
   * @param symbol
   * @return true if symbol is valid
   */
  public static boolean validSymbol(String symbol) {
    if (symbol == null) {
      return false;
    }

    if (symbol.isEmpty() || symbol.length() > 5) {
      return false;
    }

    for (char ch : symbol.toCharArray()) {
      if (!Character.isLetterOrDigit(ch)) {
        return false;
      }
    }
    return true;
  }
}
