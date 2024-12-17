package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  BinanceService fetches real metrics and can provide a chart URL.
  Repository: https://github.com/Vladymirovich/RocketScreener
*/

@Service
public class BinanceService implements DataSourceService {

    private final String apiKey;
    private final HttpClient httpClient;

    public BinanceService(Dotenv dotenv) {
        this.apiKey = dotenv.get("BINANCE_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        // Example: 
        // GET https://api.binance.com/api/v3/ticker/24hr?symbol=BTCUSDT
        // If multiple symbols, we do multiple requests or check if Binance supports multiple.
        Map<String, Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = sym.toUpperCase()+"USDT"; // example conversion
            String url = "https://api.binance.com/api/v3/ticker/24hr?symbol="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode() == 200){
                    JSONObject json = new JSONObject(resp.body());
                    double val;
                    if(metric.equals("volume")){
                        val = json.getDouble("volume");
                    } else {
                        val = json.getDouble("lastPrice");
                    }
                    result.put(sym, val);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public String fetchChartUrl(String symbol) {
        // Assume a generic chart URL from Binance or a third-party aggregator
        // For example:
        return "https://www.binance.com/en/trade/" + symbol.toUpperCase() + "_USDT";
    }
}
