package com.example.pocketmanager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeRateApi {

    public static double getRate(String from, String to) throws Exception {
        // If converting from TND, we need to use a different approach
        // Try using exchangerate-api.com free endpoint or convert through USD
        if ("TND".equals(from)) {
            return getTNDRate(to);
        }
        
        // For other currencies, use standard API
        String urlStr = "https://api.exchangerate.host/latest?base=" + from + "&symbols=" + to;
        URL url = new URL(urlStr);
        return fetchRate(url, to);
    }
    
    private static double getTNDRate(String to) throws Exception {
        // Try to get TND rate directly from exchangerate-api.com which supports TND
        // This API returns all rates in one call, so we can get both USD and EUR
        try {
            String urlStr = "https://api.exchangerate-api.com/v4/latest/TND";
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();
                
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();
                    
                    JSONObject object = new JSONObject(json.toString());
                    JSONObject rates = object.getJSONObject("rates");
                    
                    // Check if the target currency is available in the response
                    if (rates.has(to)) {
                        return rates.getDouble(to);
                    } else {
                        throw new Exception("Currency " + to + " not found in TND rates");
                    }
                } else {
                    throw new Exception("HTTP error: " + connection.getResponseCode());
                }
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            // If direct conversion fails, try converting through USD
            // This is a fallback method
            double tndToUsd = getTNDToUSDFallback();
            
            if ("USD".equals(to)) {
                return tndToUsd;
            } else if ("EUR".equals(to)) {
                // Convert USD to EUR using a reliable API
                try {
                    String urlStr = "https://api.exchangerate-api.com/v4/latest/USD";
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    try {
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        connection.connect();
                        
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()));
                            StringBuilder json = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                json.append(line);
                            }
                            reader.close();
                            
                            JSONObject object = new JSONObject(json.toString());
                            JSONObject rates = object.getJSONObject("rates");
                            if (rates.has("EUR")) {
                                double usdToEur = rates.getDouble("EUR");
                                return tndToUsd * usdToEur;
                            }
                        }
                    } finally {
                        connection.disconnect();
                    }
                } catch (Exception e2) {
                    // Fallback: approximate EUR rate (1 TND ≈ 0.29 EUR)
                    return 0.29;
                }
            }
            
            // Final fallback for other currencies
            throw new Exception("Unable to convert TND to " + to + ": " + e.getMessage());
        }
    }
    
    private static double getTNDToUSDFallback() {
        // Fallback: Use approximate rate (1 TND = 0.32 USD)
        // This is a reasonable approximation if API fails
        return 0.32;
    }
    
    private static double fetchRate(URL url, String targetCurrency) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds timeout
            connection.setReadTimeout(10000); // 10 seconds timeout
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            
            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Try to read error message
                String errorMessage = "HTTP error code: " + responseCode;
                try {
                    if (connection.getErrorStream() != null) {
                        BufferedReader errorReader = new BufferedReader(
                                new InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorJson = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorJson.append(errorLine);
                        }
                        errorReader.close();
                        if (errorJson.length() > 0) {
                            errorMessage += " - " + errorJson.toString();
                        }
                    }
                } catch (Exception e) {
                    // Ignore error reading error stream
                }
                throw new Exception(errorMessage);
            }

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            reader.close();

            JSONObject object = new JSONObject(json.toString());
            
            // Check if API returned success (some APIs use "success" field)
            if (object.has("success") && !object.getBoolean("success")) {
                throw new Exception("API returned error: " + object.optString("error", "Unknown error"));
            }
            
            JSONObject rates = object.getJSONObject("rates");
            
            if (!rates.has(targetCurrency)) {
                throw new Exception("Currency " + targetCurrency + " not found in response");
            }

            return rates.getDouble(targetCurrency);
        } finally {
            connection.disconnect();
        }
    }
}
