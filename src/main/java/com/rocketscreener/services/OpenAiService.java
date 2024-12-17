package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/*
  OpenAiService: Analyze narrative of a coin using OpenAI API.
  No placeholders. If no OPENAI_API_KEY, log error and return default.
*/

@Service
public class OpenAiService {
    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final HttpClient httpClient;
    private final String openAiApiKey;

    public OpenAiService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.openAiApiKey = dotenv.get("OPENAI_API_KEY");
        if(this.openAiApiKey == null){
            log.warn("No OPENAI_API_KEY found. Cannot perform narrative analysis.");
        }
    }

    public String analyzeNarrative(String symbol){
        if(openAiApiKey == null || openAiApiKey.isEmpty()){
            log.error("OpenAiService: No API key, returning default narrative.");
            return "No narrative available.";
        }

        // Example call to OpenAI API (ChatGPT model), pseudo code:
        String prompt = "Provide a brief, current narrative for the cryptocurrency " + symbol + ".";
        String url = "https://api.openai.com/v1/completions";
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "text-davinci-003");
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 50);
        requestBody.put("temperature", 0.7);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(resp.statusCode()==200){
                JSONObject json = new JSONObject(resp.body());
                if(json.has("choices") && json.getJSONArray("choices").length()>0){
                    return json.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                } else {
                    log.warn("OpenAiService: Unexpected response format.");
                }
            } else {
                log.error("OpenAiService: Failed to get narrative. Status: {}", resp.statusCode());
            }
        } catch(Exception e){
            log.error("OpenAiService: Error analyzing narrative.", e);
        }

        return "No narrative available due to an error.";
    }
}
