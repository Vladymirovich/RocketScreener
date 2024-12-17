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
  KrakenService:
  GET https://api.kraken.com/0/public/Ticker?pair=XBTUSD
  Map BTC -> XBTUSD, ETH -> ETHUSD etc.
*/

@Service
public class KrakenService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(KrakenService.class);
    private final HttpClient httpClient;
    private final String apiKey;

    public KrakenService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("KRAKEN_API_KEY");
        if(this.apiKey == null){
            log.info("No KRAKEN_API_KEY found, using public data.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = mapSymbol(sym);
            String url = "https://api.kraken.com/0/public/Ticker?pair="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONObject json = new JSONObject(resp.body());
                    if(json.has("result")){
                        JSONObject res = json.getJSONObject("result");
                        // key is unknown, we must find the pair key
                        String key = res.keys().next();
                        JSONObject ticker = res.getJSONObject(key);
                        // 'a' = ask array, 'b' = bid array, 'c' = last trade
                        // 'v' = volume array [volume(24h), volume(...)]
                        double val;
                        if(metric.equalsIgnoreCase("volume")){
                            val = Double.parseDouble(ticker.getJSONArray("v").getString(1));
                        } else {
                            // last trade price
                            val = Double.parseDouble(ticker.getJSONArray("c").getString(0));
                        }
                        result.put(sym, val);
                    }
                } else {
                    log.error("KrakenService: Failed to fetch {} for {}", metric, sym);
                }
            } catch(Exception e){
                log.error("KrakenService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }

    private String mapSymbol(String sym){
        // Kraken uses XBT for BTC, etc.
        if(sym.equalsIgnoreCase("BTC")) return "XBTUSD";
        if(sym.equalsIgnoreCase("ETH")) return "ETHUSD";
        return sym.toUpperCase()+"USD";
    }
}
