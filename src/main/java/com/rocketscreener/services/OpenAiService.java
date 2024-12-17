package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.net.http.*;
import java.net.URI;
import org.json.JSONObject;

/**
 * OpenAI integration for narrative analysis.
 * For example, we can send a prompt about the crypto symbol and get a summary.
 */
@Service
public class OpenAiService {
    private final HttpClient httpClient;
    private final String openAiApiKey;

    public OpenAiService(Dotenv dotenv){
        this.httpClient = HttpClient.newHttpClient();
        this.openAiApiKey = dotenv.get("OPENAI_API_KEY"); // Add in .env if needed
    }

    public String analyzeNarrative(String symbol){
        // Mock call:
        return "Narrative for " + symbol + ": This token is gaining traction due to recent developments...";
    }
}
