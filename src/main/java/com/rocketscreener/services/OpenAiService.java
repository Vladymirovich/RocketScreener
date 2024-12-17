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
  OpenAiService: Perform smart money analysis for a given coin and event.
  The response must be 13-15 words.
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
            log.warn("No OPENAI_API_KEY found. Cannot perform smart money analysis.");
        }
    }

    /**
     * Analyze smart money activity related to a coin and a specific event.
     * The prompt should request a 13-15 word summary of smart money behavior.
     */
    public String analyzeSmartMoney(String symbol, String eventType) {
        if(openAiApiKey == null || openAiApiKey.isEmpty()){
            log.error("OpenAiService: No API key, returning default analysis.");
            return "Unable to provide analysis due to missing API key.";
        }

        // Prompt focuses on smart money analysis, coin, and event:
        String prompt = "In exactly 13 to 15 words, summarize smart money behavior for " + symbol + " during " + eventType + ".";

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
                    String text = json.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                    // We should verify the word count (13-15 words).
                    int wordCount = text.split("\\s+").length;
                    if(wordCount < 13 || wordCount > 15){
                        log.warn("OpenAiService: Received text not within 13-15 words: {}", text);
                    }
                    return text;
                } else {
                    log.warn("OpenAiService: Unexpected response format.");
                }
            } else {
                log.error("OpenAiService: Failed to get analysis. Status: {}", resp.statusCode());
            }
        } catch(Exception e){
            log.error("OpenAiService: Error analyzing smart money.", e);
        }

        return "No smart money analysis available due to an error.";
    }
}
