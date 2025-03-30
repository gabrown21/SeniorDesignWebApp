import * as React from "react"
import { useLoaderData } from 'react-router-dom';

interface PredictionData {
  lstm_predicted_price: number;
  nlp_sentiment_score: number;
  rl_recommendation: string;
  updated_at: string;
  xgboost_signal: string;
}

export const Predictions: React.FC = () => {
  const predictions = useLoaderData() as PredictionData;
    const getColor = (value: string | number) => {
        if (typeof value === 'number') {
          return value > 0.5 ? 'green' : 'red';
        }
        return value === "Buy" || value === "Up" ? 'green' : 'red';
      };
    
      return (
        <div style={{ padding: "20px", fontFamily: "Arial, sans-serif", background: "#f0f4f8", borderRadius: "12px" }}>
        <h2 style={{ textAlign: "center" }}>Trading Recommendations for {predictions.ticker}</h2>
        <div style={{ display: "flex", justifyContent: "space-around", flexWrap: "wrap", marginTop: "20px" }}>
          <div style={{ background: "#ffffff", borderRadius: "8px", padding: "16px", width: "250px", margin: "10px", textAlign: "center", boxShadow: "0 4px 6px rgba(0,0,0,0.1)" }}>
            <h3>LSTM Predicted Price</h3>
            <p style={{ fontSize: "24px", color: "#007bff" }}>${predictions.lstm_predicted_price.toFixed(2)}</p>
          </div>
          <div style={{ background: "#ffffff", borderRadius: "8px", padding: "16px", width: "250px", margin: "10px", textAlign: "center", boxShadow: "0 4px 6px rgba(0,0,0,0.1)" }}>
            <h3>XGBoost Signal</h3>
            <p style={{ fontSize: "24px", color: getColor(predictions.xgboost_signal) }}>{predictions.xgboost_signal}</p>
          </div>
          <div style={{ background: "#ffffff", borderRadius: "8px", padding: "16px", width: "250px", margin: "10px", textAlign: "center", boxShadow: "0 4px 6px rgba(0,0,0,0.1)" }}>
            <h3>NLP Sentiment Score</h3>
            <p style={{ fontSize: "24px", color: getColor(predictions.nlp_sentiment_score) }}>{predictions.nlp_sentiment_score.toFixed(2)}</p>
          </div>
          <div style={{ background: "#ffffff", borderRadius: "8px", padding: "16px", width: "250px", margin: "10px", textAlign: "center", boxShadow: "0 4px 6px rgba(0,0,0,0.1)" }}>
            <h3>Reinforcement Learning Recommendation</h3>
            <p style={{ fontSize: "24px", color: getColor(predictions.rl_recommendation) }}>{predictions.rl_recommendation}</p>
          </div>
        </div>
        <div style={{ textAlign: "center", marginTop: "20px", fontSize: "14px", color: "#555" }}>
          Last Updated: {new Date(predictions.updated_at).toLocaleString()}
        </div>
      </div>
  );
};