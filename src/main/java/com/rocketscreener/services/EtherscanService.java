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
import java.util.Map;

/*
  EtherscanService: Fetch recent transactions from Ethereum network.
  No placeholders, if no key, log warning.
*/

@Service
public class EtherscanService {
    private static final Logger log = LoggerFactory.getLogger(EtherscanService.class);

    private final HttpClient httpClient;
    private final String etherscanApiKey;

    public EtherscanService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.etherscanApiKey = dotenv.get("ETHERSCAN_API_KEY");
        if(this.etherscanApiKey == null){
            log.warn("No ETHERSCAN_API_KEY found. Limited functionality.");
        }
    }

    public Map<String,Object> getRecentTransactions(String symbol){
        // Example: https://api.etherscan.io/api?module=account&action=txlist&address=<contract>&apikey=<key>
        // Without a real address mapping for symbol, we assume symbol to address mapping is known.
        // For demonstration, log error if we can't map symbol.
        Map<String,Object> result = new HashMap<>();
        String address = mapSymbolToAddress(symbol);
        if(address == null){
            log.error("EtherscanService: No address mapping for symbol {}", symbol);
            return result;
        }

        String url = "https://api.etherscan.io/api?module=account&action=txlist&address="+address+"&sort=desc";
        if(this.etherscanApiKey != null && !this.etherscanApiKey.isEmpty()){
            url += "&apikey="+this.etherscanApiKey;
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(resp.statusCode()==200){
                JSONObject json = new JSONObject(resp.body());
                if(json.getString("status").equals("1")){
                    result.put("transactions", json.getJSONArray("result").toList());
                } else {
                    log.warn("EtherscanService: No transactions found or error. Message: {}", json.optString("message"));
                }
            } else {
                log.error("EtherscanService: Failed to fetch transactions. Status: {}", resp.statusCode());
            }
        } catch(Exception e){
            log.error("EtherscanService: Error fetching transactions.", e);
        }

        return result;
    }

    private String mapSymbolToAddress(String symbol){
        // Real logic: maybe query DB or predefined map
        // For demonstration, assume only "ETH" supported
        if(symbol.equalsIgnoreCase("ETH")) return "0x00000000219ab540356cBB839Cbe05303d7705Fa";
        return null;
    }
}
