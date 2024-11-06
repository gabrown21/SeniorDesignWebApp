import * as React from "react";
import { useParams, useLoaderData, Link } from "react-router-dom";

export const AverageVolume: React.FC = () => {
  const { symbol } = useParams<{ symbol: string }>();
  const averageVolume = useLoaderData() as number;

  const formattedVolume = new Intl.NumberFormat("en-US", {
    maximumFractionDigits: 2,
  }).format(averageVolume);

  return (
    <div>
      <h2>Average Volume for {symbol}:</h2>
      <p>{formattedVolume}</p>
      <Link to="/symbols">Back to Symbols</Link>
    </div>
  );
};