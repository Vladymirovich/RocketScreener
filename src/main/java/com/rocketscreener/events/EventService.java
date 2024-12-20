package com.rocketscreener.events;

import com.rocketscreener.storage.EventRepository;
import com.rocketscreener.templates.TemplateService;
import com.rocketscreener.controllers.PublicBotController;
import com.rocketscreener.services.CoinMarketCapService;
import com.rocketscreener.services.BinanceService;
import com.rocketscreener.services.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * EventService:
 * Manages event storage, analysis, and notifications.
 * Integrates OpenAiService for smart money analysis.
 */
@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepo;
    private final TemplateService templateService;
    private final PublicBotController publicBot;
    private final CoinMarketCapService cmcService;
    private final BinanceService binanceService;
    private final OpenAiService openAiService;
    private final Dotenv dotenv;

    public EventService(EventRepository eventRepo,
                        TemplateService templateService,
                        PublicBotController publicBot,
                        CoinMarketCapService cmcService,
                        BinanceService binanceService,
                        OpenAiService openAiService,
                        Dotenv dotenv) {
        this.eventRepo = eventRepo;
        this.templateService = templateService;
        this.publicBot = publicBot;
        this.cmcService = cmcService;
        this.binanceService = binanceService;
        this.openAiService = openAiService;
        this.dotenv = dotenv;
    }

    /**
     * Records an event and triggers a notification.
     *
     * @param eventType Type of the event.
     * @param symbol    Symbol associated with the event.
     * @param source    Source of the event.
     * @param details   Additional details about the event.
     */
    public void recordEvent(String eventType, String symbol, String source, JSONObject details) {
        eventRepo.addEvent(eventType, symbol, source, details);
        sendNotification(eventType, symbol, source, details);
    }

    /**
     * Sends a notification for a recorded event.
     *
     * @param eventType Type of the event.
     * @param symbol    Symbol associated with the event.
     * @param source    Source of the event.
     * @param details   Additional details about the event.
     */
    private void sendNotification(String eventType, String symbol, String source, JSONObject details) {
        String lang = dotenv.get("DEFAULT_LANGUAGE", "en");
        String templateName = eventType.toLowerCase() + "_notification";

        String chartUrl = fetchChartUrl(source, symbol);
        String smartMoneyAnalysis = openAiService.analyzeSmartMoney(symbol, eventType);

        // Template placeholders: {0}=symbol, {1}=source, {2}=chartUrl, {3}=smartMoneyAnalysis
        String message = templateService.render(templateName, lang, symbol, source, chartUrl, smartMoneyAnalysis);

        String publicChatId = dotenv.get("PUBLIC_CHAT_ID");
        if (publicChatId == null || publicChatId.isEmpty()) {
            log.error("PUBLIC_CHAT_ID not found in .env, cannot send event notification.");
            return;
        }

        publicBot.sendNotification(publicChatId, templateName, lang, symbol, source, chartUrl, smartMoneyAnalysis);
    }

    /**
     * Fetches the chart URL based on the event source.
     *
     * @param source Event source (e.g., "coinmarketcap" or "binance").
     * @param symbol Symbol associated with the event.
     * @return Chart URL.
     */
    private String fetchChartUrl(String source, String symbol) {
        switch (source.toLowerCase()) {
            case "coinmarketcap":
                return cmcService.fetchChartUrl(symbol);
            case "binance":
                return binanceService.fetchChartUrl(symbol);
            default:
                return cmcService.fetchChartUrl(symbol);
        }
    }
}
