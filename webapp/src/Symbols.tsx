import * as React from "react";
import { useLoaderData, Link, Outlet } from "react-router-dom";

export const Symbols: React.FC = () => {
  // Get the symbols as a string array from the data returned by the loader
  // Display a header identifying this as the "Symbols" page
  // and show a list of the symbols returned by the web service.
  // For each symbol, provide a link to its price and average volume sub-views.
  const symbols = useLoaderData() as string[];
  const [expandedSymbol, setExpandedSymbol] = React.useState<string | null>(null);

  const toggleSymbol = (symbol: string) => {
    setExpandedSymbol((prev) => (prev === symbol ? null : symbol));
  };
  return (
    <div>
      <h2>Symbols</h2>
      <ul data-testid="symbols-list">
        {symbols.map((symbol) => (
          <li key={symbol}>
            <button
              onClick={() => toggleSymbol(symbol)}
            >
              <span>{symbol}</span>
              <span style={{ transform: expandedSymbol === symbol ? "rotate(90deg)" : "rotate(0deg)" }}>
                ▼
              </span>
            </button>

            {expandedSymbol === symbol && (
              <div>
                <Link to={`${symbol}/price`} style={{ display: "block"}}>
                  Price
                </Link>
                <Link to={`${symbol}/averagevolume`} style={{ display: "block"}}>
                  Average Volume
                </Link>
              </div>
            )}
          </li>
        ))}
      </ul>

      <Outlet />
      <Link to="/">Back</Link>
    </div>
  );
}
