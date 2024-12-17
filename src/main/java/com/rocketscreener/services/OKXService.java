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
  OKXService:
  GET https://www.okx.com/api/v5/market/ticker?instId=BTC-USDT
  Map symbol to BTC-USDT, etc.
*/

@Service
public class OKXService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(OKXService.class);
    private final HttpClient httpClient;
    private final String apiKey;

    public OKXService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("OKX_API_KEY");
        if(this.apiKey == null){
            log.info("No OKX_API_KEY found, using public endpoints if allowed.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = sym.toUpperCase()+"-USDT";
            String url = "https://www.okx.com/api/v5/market/ticker?instId="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONObject json = new JSONObject(resp.body());
                    if(json.has("data") && json.getJSONArray("data").length()>0){
                        JSONObject ticker = json.getJSONArray("data").getJSONObject(0);
                        double val;
                        if(metric.equalsIgnoreCase("volume")){
                            val = Double.parseDouble(ticker.getString("vol24h"));
                        } else {
                            val = Double.parseDouble(ticker.getString("last"));
                        }
                        result.put(sym, val);
                    }
                } else {
                    log.error("OKXService: Failed to fetch {} for {}", metric, sym);
                }
            } catch(Exception e){
                log.error("OKXService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }
}
