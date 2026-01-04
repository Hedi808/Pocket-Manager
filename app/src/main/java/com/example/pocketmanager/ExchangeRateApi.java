package com.example.pocketmanager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeRateApi {

    public static double getRate(String from, String to) throws Exception {

        String urlStr = "https://api.frankfurter.app/latest?from=" + from + "&to=" + to;
        URL url = new URL(urlStr);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        reader.close();

        JSONObject object = new JSONObject(json.toString());
        JSONObject rates = object.getJSONObject("rates");

        return rates.getDouble(to);
    }
}
