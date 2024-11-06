import * as React from "react";
import { useLoaderData, Link } from "react-router-dom";

export const Symbols: React.FC = () => {
  // Get the symbols as a string array from the data returned by the loader
  // Display a header identifying this as the "Symbols" page
  // and show a list of the symbols returned by the web service.
  // For each symbol, provide a link to its price and average volume sub-views.
  const symbols = useLoaderData() as string[];

  return (
    <div>
      <h2>Symbols</h2>
      <ul data-testid="symbols-list">
        {symbols.map((symbol) => (
          <li key={symbol}>
            {symbol}
            <div>
              <Link to={`${symbol}/price`} style={{ marginRight: "10px", color: "white", textDecoration: "underline" }}>
                Price
              </Link>
              <Link to={`${symbol}/averagevolume`} style={{ color: "white", textDecoration: "underline" }}>
                Average Volume
              </Link>
            </div>
          </li>
        ))}
      </ul>

      <Link to="/">Back</Link>
    </div>
  );
}
