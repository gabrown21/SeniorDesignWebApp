import { http, HttpResponse } from "msw"
import { API_URL } from "../constants"

export const handlers = [
  http.get(`${API_URL}/symbols`, () => {
    return HttpResponse.json({
      symbols: ["AAPL"],
    })
  }),
]
