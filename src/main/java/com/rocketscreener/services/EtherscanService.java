package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

@Service
public class EtherscanService {
    private final HttpClient httpClient;
    private final String etherscanApiKey;

    public EtherscanService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.etherscanApiKey = dotenv.get("ETHERSCAN_API_KEY");
    }

    public Map<String,Object> getRecentTransactions(String symbol){
        // Mock call
        Map<String,Object> result = new HashMap<>();
        result.put("transactions", "Mock transactions for "+symbol);
        return result;
    }
}
