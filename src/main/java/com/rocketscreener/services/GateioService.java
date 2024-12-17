package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  GateioService:
  GET https://api.gateio.ws/api/v4/spot/tickers?currency_pair=BTC_USDT
  Returns array of objects with "last", "quoteVolume"
*/

@Service
public class GateioService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(GateioService.class);
    private final HttpClient httpClient;
    private final String apiKey;

    public GateioService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("GATEIO_API_KEY");
        if(this.apiKey == null){
            log.info("No GATEIO_API_KEY found, using public endpoints.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = sym.toUpperCase()+"_USDT";
            String url = "https://api.gateio.ws/api/v4/spot/tickers?currency_pair="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONArray arr = new JSONArray(resp.body());
                    if(arr.length()>0){
                        // { "currency_pair":"BTC_USDT","last":"XXXX","quote_volume":"XXX"...}
                        var obj = arr.getJSONObject(0);
                        double val;
                        if(metric.equalsIgnoreCase("volume")){
                            val = Double.parseDouble(obj.getString("quote_volume"));
                        } else {
                            val = Double.parseDouble(obj.getString("last"));
                        }
                        result.put(sym, val);
                    }
                } else {
                    log.error("GateioService: Failed to fetch {} for {}", metric, sym);
                }
            } catch(Exception e){
                log.error("GateioService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }
}
