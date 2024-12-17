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
  BitfinexService:
  GET https://api-pub.bitfinex.com/v2/tickers?symbols=tBTCUSD
  Returns array: [ "tBTCUSD", BID, BID_SIZE, ASK, ASK_SIZE, DAILY_CHANGE,...
  Index known from docs: 
   last price at index 7
   volume at index 8
*/

@Service
public class BitfinexService implements DataSourceService {
    private static final Logger log = LoggerFactory.getLogger(BitfinexService.class);
    private final HttpClient httpClient;
    private final String apiKey;

    public BitfinexService(Dotenv dotenv) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = dotenv.get("BITFINEX_API_KEY");
        if(this.apiKey == null){
            log.info("No BITFINEX_API_KEY found, using public endpoints.");
        }
    }

    @Override
    public Map<String, Double> fetchCurrentMetrics(String metric, List<String> symbols) {
        Map<String,Double> result = new HashMap<>();
        for(String sym: symbols){
            String pair = "t"+sym.toUpperCase()+"USD";
            String url = "https://api-pub.bitfinex.com/v2/tickers?symbols="+pair;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            try {
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if(resp.statusCode()==200){
                    JSONArray arr = new JSONArray(resp.body());
                    if(arr.length()>0){
                        JSONArray ticker = arr.getJSONArray(0);
                        // ticker format: [ SYMBOL, BID, BID_SIZE, ASK, ASK_SIZE, DAILY_CHANGE, ..., LAST_PRICE(7), VOLUME(8), ...]
                        double val;
                        if(metric.equalsIgnoreCase("volume")){
                            val = ticker.getDouble(8);
                        } else {
                            val = ticker.getDouble(7);
                        }
                        result.put(sym, val);
                    }
                } else {
                    log.error("BitfinexService: Failed to fetch {} for {}", metric, sym);
                }
            } catch(Exception e){
                log.error("BitfinexService: Error fetching data for {}", sym, e);
            }
        }
        return result;
    }
}
