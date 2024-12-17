package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  ByBitService: Fetch price/volume from ByBit public API.
  Example endpoint: https://api.bybit.com/v2/public/tickers?symbol=BTCUSD
  If symbol is different (e.g. BTC-USDT), adapt symbol formatting.
*/

@Service
public class ByBitService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(ByBitService.class);
    private final HttpClient httpClient;
    private final String apiKey; // If needed, else can be null

    public ByBitService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("BYBIT_API_KEY");
        if(this.apiKey == null){
            log.info("No BYBIT_API_KEY found, proceeding with public endpoints if available.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym : symbols){
            // ByBit uses symbols like BTCUSD, ETHUSD for futures
            String pair = mapSymbol(sym);
            String url = "https://api.bybit.com/v2/public/tickers?symbol="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONObject json = new JSONObject(resp.body());
                    if(json.has("result") && json.getJSONArray("result").length()>0){
                        JSONObject ticker = json.getJSONArray("result").getJSONObject(0);
                        double val;
                        if(metric.equalsIgnoreCase("volume")){
                            val = Double.parseDouble(ticker.getString("volume_24h"));
                        } else {
                            val = Double.parseDouble(ticker.getString("last_price"));
                        }
                        result.put(sym, val);
                    }
                } else {
                    log.error("ByBitService: Failed to get data for {}", sym);
                }
            } catch(Exception e){
                log.error("ByBitService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }

    private String mapSymbol(String sym){
        // For simplicity assume BTC -> BTCUSD if metric from Bybit futures
        // In reality, you'd map symbol to a known contract or pair
        if(sym.equalsIgnoreCase("BTC")) return "BTCUSD";
        if(sym.equalsIgnoreCase("ETH")) return "ETHUSD";
        // Default fallback:
        return sym.toUpperCase()+"USD";
    }
}
