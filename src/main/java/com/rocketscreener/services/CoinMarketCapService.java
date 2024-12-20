package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoinMarketCapService implements DataSourceService {

    private static final Logger log = LoggerFactory.getLogger(CoinMarketCapService.class);

    private final String apiKey;
    private final HttpClient httpClient;

    public CoinMarketCapService(Dotenv dotenv) {
        this.apiKey = dotenv.get("COINMARKETCAP_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            log.error("API_KEY for CoinMarketCap is missing in the environment variables.");
            throw new IllegalStateException("API_KEY is required for CoinMarketCapService.");
        }
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String, Double> result = new HashMap<>();
        if (symbols == null || symbols.isEmpty()) {
            log.warn("Symbol list is empty. No data will be fetched.");
            return result;
        }

        String symbolParam = String.join(",", symbols);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + symbolParam;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-CMC_PRO_API_KEY", this.apiKey)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONObject data = json.getJSONObject("data");

                for (String symbol : symbols) {
                    try {
                        JSONObject coin = data.getJSONObject(symbol.toUpperCase());
                        JSONObject quote = coin.getJSONObject("quote").getJSONObject("USD");

                        double value = metric.equalsIgnoreCase("volume") ?
                                quote.getDouble("volume_24h") :
                                quote.getDouble("price");

                        result.put(symbol, value);
                    } catch (Exception e) {
                        log.warn("Could not fetch data for symbol: {}", symbol, e);
                    }
                }
            } else {
                log.error("Failed to fetch data from CoinMarketCap. HTTP Status Code: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error during CoinMarketCap API request", e);
        }

        return result;
    }

    public String fetchChartUrl(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            log.warn("Symbol is empty or null. Cannot generate chart URL.");
            return "";
        }
        return "https://coinmarketcap.com/currencies/" + symbol.toLowerCase() + "/";
    }
}
