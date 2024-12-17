package com.rocketscreener.events;

import com.rocketscreener.storage.EventRepository;
import com.rocketscreener.templates.TemplateService;
import com.rocketscreener.controllers.PublicBotController;
import com.rocketscreener.services.CoinMarketCapService;
import com.rocketscreener.services.BinanceService;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
  EventService: records events and sends notifications.
  No placeholders, real logic. If PUBLIC_CHAT_ID not found, log error and skip.
*/

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepo;
    private final TemplateService templateService;
    private final PublicBotController publicBot;
    private final CoinMarketCapService cmcService;
    private final BinanceService binanceService;
    private final Dotenv dotenv;

    public EventService(EventRepository eventRepo,
                        TemplateService templateService,
                        PublicBotController publicBot,
                        CoinMarketCapService cmcService,
                        BinanceService binanceService,
                        Dotenv dotenv) {
        this.eventRepo = eventRepo;
        this.templateService = templateService;
        this.publicBot = publicBot;
        this.cmcService = cmcService;
        this.binanceService = binanceService;
        this.dotenv = dotenv;
    }

    public void recordEvent(String eventType, String symbol, String source, JSONObject details) {
        eventRepo.addEvent(eventType, symbol, source, details);
        sendNotification(eventType, symbol, source, details);
    }

    private void sendNotification(String eventType, String symbol, String source, JSONObject details) {
        String lang = dotenv.get("DEFAULT_LANGUAGE","en");
        String templateName = eventType.toLowerCase() + "_notification";

        String chartUrl;
        if(source.equalsIgnoreCase("coinmarketcap")){
            chartUrl = cmcService.fetchChartUrl(symbol);
        } else if(source.equalsIgnoreCase("binance")){
            chartUrl = binanceService.fetchChartUrl(symbol);
        } else {
            chartUrl = cmcService.fetchChartUrl(symbol);
        }

        String msg = templateService.render(templateName, lang, symbol, source, chartUrl);

        String publicChatId = dotenv.get("PUBLIC_CHAT_ID");
        if(publicChatId == null || publicChatId.isEmpty()){
            log.error("PUBLIC_CHAT_ID not found in .env, cannot send event notification.");
            return;
        }

        publicBot.sendNotification(publicChatId, msg);
    }
}
