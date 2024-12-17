package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArkhamService {
    private final HttpClient httpClient;
    private final String arkhamApiKey;

    public ArkhamService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.arkhamApiKey = dotenv.get("ARKHAM_API_KEY");
    }

    public Map<String, Object> getLargeTransfers(String symbol){
        // Mock call
        // GET https://api.arkhamintelligence.com/v1/transfers?symbol=...
        Map<String,Object> result = new HashMap<>();
        result.put("transfers", "No real data, mock");
        return result;
    }
}
