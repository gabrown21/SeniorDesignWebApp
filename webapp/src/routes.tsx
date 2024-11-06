import type { RouteObject } from "react-router-dom"
import { StockApp } from "./StockApp"
import { MostActive } from "./MostActive"
import { Symbols } from "./Symbols"
import { Home } from "./Home"
import { API_URL } from "./constants"

// This object describes the structure of the app: its possible screens, parameters,
// and data loading operations.
export const routes: RouteObject[] = [
  {
    path: "",
    element: <StockApp />,
    // Child routes are given a place to render within the parent via the <Outlet /> component
    children: [
      {
        path: "",
        element: <Home />,
      },
      {
        path: "mostactive",
        element: <MostActive />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          try {
            const response = await fetch(`${API_URL}/most-active`);
            if (!response.ok) {
              throw new Error("Failed fetching most active stock.\n");
            }
            const data = await response.json();
            return data.mostActiveStock;
          } catch (error) {
            throw new Error("Failed to load most active stock.\n" + (error as Error).message);
          }
        },
        // Provide JSX to show if the data fails to load
        errorElement: null,
      },
      {
        path: "symbols",
        element: <Symbols />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          return null
        },
        // Provide JSX to show if the data fails to load
        errorElement: <div>Failed to load symbols</div>,
        // Child routes can continue to be nested
        children: [],
      },
    ],
  },
]
