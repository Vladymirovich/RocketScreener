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
        Map<String, Double> result = new HashMap<>();
        if(symbols.isEmpty()) return result;

        String symbolParam = String.join(",", symbols);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol="+symbolParam;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-CMC_PRO_API_KEY", this.apiKey)
                .GET()
                .build();
        try {
            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(resp.statusCode()==200){
                JSONObject json = new JSONObject(resp.body());
                JSONObject data = json.getJSONObject("data");
                for(String sym: symbols){
                    JSONObject coin = data.getJSONObject(sym.toUpperCase());
                    JSONObject quote = coin.getJSONObject("quote").getJSONObject("USD");
                    double val;
                    if(metric.equalsIgnoreCase("volume")){
                        val = quote.getDouble("volume_24h");
                    } else {
                        val = quote.getDouble("price");
                    }
                    result.put(sym, val);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String fetchChartUrl(String symbol) {
        // Realistic approach: redirect user to coin page
        return "https://coinmarketcap.com/currencies/" + symbol.toLowerCase() + "/";
    }
}
