import * as React from "react"
import { useLoaderData, Link } from "react-router-dom";
export const MostActive: React.FC = () => {
  // Get the symbol value returned by the route's loader as a string

  // Show a header that says "Most Active" followed by the symbol returned
  // by the web service.
  const symbol = useLoaderData() as string;

  return (
    <div>
      <h2>Most Active</h2>
      <p>{symbol}</p>

      <Link to="/">Back</Link>
    </div>
  );
}
