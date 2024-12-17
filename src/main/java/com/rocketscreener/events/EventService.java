package com.rocketscreener.events;

import com.rocketscreener.storage.EventRepository;
import com.rocketscreener.templates.TemplateService;
import com.rocketscreener.controllers.PublicBotController;
import com.rocketscreener.services.CoinMarketCapService;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

/*
  EventService now integrates with CoinMarketCapService to fetch chart URL
  and includes it into the notification message.
  Repository: https://github.com/Vladymirovich/RocketScreener
*/

@Service
public class EventService {
    private final EventRepository eventRepo;
    private final TemplateService templateService;
    private final PublicBotController publicBot;
    private final CoinMarketCapService cmcService;
    private final Dotenv dotenv;

    public EventService(EventRepository eventRepo,
                        TemplateService templateService,
                        PublicBotController publicBot,
                        CoinMarketCapService cmcService,
                        Dotenv dotenv) {
        this.eventRepo = eventRepo;
        this.templateService = templateService;
        this.publicBot = publicBot;
        this.cmcService = cmcService;
        this.dotenv = dotenv;
    }

    public void recordEvent(String eventType, String symbol, String source, JSONObject details) {
        eventRepo.addEvent(eventType, symbol, source, details);
        // After recording event, send notification:
        sendNotification(eventType, symbol, source, details);
    }

    private void sendNotification(String eventType, String symbol, String source, JSONObject details) {
        // Determine language from env
        String lang = dotenv.get("DEFAULT_LANGUAGE","en");
        String templateName = eventType.toLowerCase() + "_notification";
        // Fetch chart url from CoinMarketCap (assuming source is known and symbol listed)
        String chartUrl = cmcService.fetchChartUrl(symbol);

        // Prepare message using template
        // Template might have placeholders like {0}=symbol, {1}=source, {2}=chartUrl
        String msg = templateService.render(templateName, lang, symbol, source, chartUrl);

        // Send to public bot
        // In a real scenario we should have a list of subscribers or a public channel chat_id
        String publicChatId = dotenv.get("PUBLIC_CHAT_ID","PUBLIC_CHAT_ID_PLACEHOLDER");
        publicBot.sendNotification(publicChatId, msg);
    }
}
