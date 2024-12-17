package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.http.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoinMarketCapService implements DataSourceService {
    private final String apiKey;
    private final HttpClient httpClient;

    public CoinMarketCapService(Dotenv dotenv) {
        this.apiKey = dotenv.get("COINMARKETCAP_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        // Simplified call. In reality, call CMC API:
        // GET https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=BTC,ETH...
        // and parse JSON.
        // Here just return dummy data for demonstration.
        Map<String, Double> result = new HashMap<>();
        for(String sym: symbols){
            result.put(sym, 1000.0); // dummy value
        }
        return result;
    }
}
