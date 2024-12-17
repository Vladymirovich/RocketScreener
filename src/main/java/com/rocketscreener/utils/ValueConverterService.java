package com.rocketscreener.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/*
  ValueConverterService: Convert USD price to another currency using a real API (e.g. exchangerate API).
  Repository: https://github.com/Vladymirovich/RocketScreener
*/

@Service
public class ValueConverterService {
    private final HttpClient httpClient;
    private final String exchangeApiKey;

    public ValueConverterService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.exchangeApiKey = dotenv.get("EXCHANGE_API_KEY"); // Add in .env if needed
    }

    public double convertUSDTo(String currency, double amount) {
        // Example API call:
        // GET https://api.exchangerate-api.com/v4/latest/USD
        // Parse rates and multiply amount by rate
        // If no real API key or URL, use a known public API endpoint
        String url = "https://api.exchangerate-api.com/v4/latest/USD";
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(resp.statusCode()==200){
                JSONObject json = new JSONObject(resp.body());
                JSONObject rates = json.getJSONObject("rates");
                if(rates.has(currency)){
                    double rate = rates.getDouble(currency);
                    return amount * rate;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        // If error or currency not found, return amount as is.
        return amount;
    }
}
