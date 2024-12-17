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
  CoinbaseService: Public endpoint:
  GET https://api.exchange.coinbase.com/products/BTC-USD/ticker
  If symbol different, change pair to <SYM>-USD.
*/

@Service
public class CoinbaseService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(CoinbaseService.class);
    private final HttpClient httpClient;
    private final String apiKey; // If needed, else null

    public CoinbaseService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("COINBASE_API_KEY");
        if(this.apiKey == null){
            log.info("No COINBASE_API_KEY provided, using public data.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = sym.toUpperCase()+"-USD";
            String url = "https://api.exchange.coinbase.com/products/"+pair+"/ticker";
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try{
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONObject json = new JSONObject(resp.body());
                    double val;
                    if(metric.equalsIgnoreCase("volume")){
                        val = Double.parseDouble(json.getString("volume"));
                    } else {
                        val = Double.parseDouble(json.getString("price"));
                    }
                    result.put(sym, val);
                } else {
                    log.error("CoinbaseService: Failed to fetch {} for {}", metric, sym);
                }
            } catch(Exception e){
                log.error("CoinbaseService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }
}
