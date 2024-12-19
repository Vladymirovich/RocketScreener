package com.rocketscreener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.rocketscreener.controllers.AdminBotController;
import com.rocketscreener.controllers.PublicBotController;

@Configuration
public class TelegramBotsConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(AdminBotController adminBot, PublicBotController publicBot) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(adminBot);
        botsApi.registerBot(publicBot);
        return botsApi;
    }
}
