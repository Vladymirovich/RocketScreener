package com.rocketscreener.config;

import com.rocketscreener.controllers.AdminBotController;
import com.rocketscreener.controllers.PublicBotController;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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
