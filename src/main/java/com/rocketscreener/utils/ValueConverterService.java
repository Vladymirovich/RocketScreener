package com.rocketscreener.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/*
  ValueConverterService: Converts USD price to another currency via real API.
  No placeholders, if no key or rates missing, logs error and returns original amount.
*/

@Service
public class ValueConverterService {
    private static final Logger log = LoggerFactory.getLogger(ValueConverterService.class);

    private final HttpClient httpClient;
    private final String exchangeApiKey;

    public ValueConverterService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.exchangeApiKey = dotenv.get("EXCHANGE_API_KEY");
        if(this.exchangeApiKey == null){
            log.warn("No EXCHANGE_API_KEY found. Conversion will use public endpoint without auth.");
        }
    }

    public double convertUSDTo(String currency, double amount) {
        if(currency == null || currency.isEmpty()){
            log.error("Invalid target currency for conversion.");
            return amount;
        }
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
                } else {
                    log.error("Currency {} not found in exchange rates.", currency);
                }
            } else {
                log.error("Failed to fetch exchange rates. Status: {}", resp.statusCode());
            }
        } catch(Exception e){
            log.error("Error converting currency: ", e);
        }
        return amount;
    }
}
