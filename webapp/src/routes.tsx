import type { RouteObject } from "react-router-dom"
import { StockApp } from "./StockApp"
import { MostActive } from "./MostActive"
import { Symbols } from "./Symbols"
import { Home } from "./Home"
import { API_URL } from "./constants"
import { Price } from "./Price"
import {AverageVolume} from "./AverageVolume"
import { Predictions } from "./Predictions";  

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
            const response = await fetch(`${API_URL}/mostactive`);
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
        errorElement: <div>Failed loading most-active stock</div>,
      },
      {
      path: "predictions",
      element: <Predictions />,
      loader: async () => {
        try {
          const response = await fetch(`${API_URL}/predictions`);
          console.log("API response status:", response.status); // log status
          const data = await response.json();
          console.log("Data received from API:", data); // log data received
          if (!response.ok) {
            throw new Error("Failed fetching predictions");
          }
          return data.predictions[0];  
        } catch (error) {
          throw new Error("Failed to load predictions.\n" + (error as Error).message);
        }
      },
      errorElement: <div>Failed loading predictions</div>,
    },
      {
        path: "symbols",
        element: <Symbols />,
        loader: async () => {
          // Fetch the most active symbol from the API
          // Convert the response to JSON
          // Return the most active symbol
          try {
            const response = await fetch(`${API_URL}/symbols`);
            if (!response.ok) {
              throw new Error("Failed to fetch symbols");
            }
            const data = await response.json();
            return data.symbols;
          } catch (error) {
            throw new Error("Failed to load symbols.\n "+ (error as Error).message);
          }
        },
        // Provide JSX to show if the data fails to load
        errorElement: <div>Failed to load symbols</div>,
        // Child routes can continue to be nested
        children: [
          {
            path: ":symbol/price",
            element: <Price />,
            loader: async ({ params }) => {
              const response = await fetch(`${API_URL}/price/${params.symbol}`);
              const data = await response.json();
              return data.currentPrice;
            },
            errorElement: <div>Failed to load price.</div>,
          },
          {
            path: ":symbol/averagevolume",
            element: <AverageVolume />,
            loader: async ({ params }) => {
              const response = await fetch(`${API_URL}/averagevolume/${params.symbol}`);
              const data = await response.json();
              return data.averageVolumePerSecond;
            },
            errorElement: <div>Failed to load average volume data.</div>,
          }
        ],
      },
    ],
  },
]
