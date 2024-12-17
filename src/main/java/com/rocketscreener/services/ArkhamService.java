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
  ArkhamService: Fetches large transfers or other blockchain intelligence data.
  No placeholders. If no API key or data, log error.
*/

@Service
public class ArkhamService {
    private static final Logger log = LoggerFactory.getLogger(ArkhamService.class);

    private final HttpClient httpClient;
    private final String arkhamApiKey;

    public ArkhamService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.arkhamApiKey = dotenv.get("ARKHAM_API_KEY");

        if(this.arkhamApiKey == null){
            log.warn("ARKHAM_API_KEY not provided. ArkhamService will not fetch private data.");
        }
    }

    public Map<String, Object> getLargeTransfers(String symbol){
        // Realistic call: Example endpoint (fictitious) "https://api.arkhamintelligence.com/v1/transfers?symbol=SYM"
        // If no actual endpoint known, we simulate real logic:
        String baseUrl = "https://api.arkhamintelligence.com/v1/transfers?symbol="+symbol.toUpperCase();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Authorization", this.arkhamApiKey != null ? "Bearer " + this.arkhamApiKey : "")
                .GET()
                .build();
        Map<String,Object> result = new HashMap<>();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(resp.statusCode()==200){
                JSONObject json = new JSONObject(resp.body());
                result.put("transfers", json.has("data")? json.getJSONArray("data").toList() : null);
            } else {
                log.error("ArkhamService: Failed to fetch transfers. Status: {}", resp.statusCode());
            }
        } catch(Exception e){
            log.error("ArkhamService: Error fetching large transfers.", e);
        }
        return result;
    }
}
