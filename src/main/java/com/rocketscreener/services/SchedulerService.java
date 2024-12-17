package com.rocketscreener.services;

import com.rocketscreener.controllers.PublicBotController;
import com.rocketscreener.templates.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {
    private final PublicBotController publicBot;
    private final TemplateService templateService;
    private final Dotenv dotenv;

    // For demo: public bot will send to a fixed chat or we store chatIds in DB
    // Let's assume we have a known public chat ID (In reality we need to store user chat ids who started the bot)
    private final String publicChatId = "PUBLIC_CHAT_ID_PLACEHOLDER";

    public SchedulerService(PublicBotController publicBot, TemplateService templateService, Dotenv dotenv) {
        this.publicBot = publicBot;
        this.templateService = templateService;
        this.dotenv = dotenv;
    }

    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9:00
    public void dailyReport() {
        String lang = dotenv.get("DEFAULT_LANGUAGE","en");
        String msg = templateService.render("daily_overview", lang);
        publicBot.sendNotification(publicChatId, msg);
    }

    @Scheduled(cron = "0 0 20 ? * SUN") // Every Sunday 20:00
    public void weeklyReport() {
        String lang = dotenv.get("DEFAULT_LANGUAGE","en");
        String msg = templateService.render("weekly_overview", lang);
        publicBot.sendNotification(publicChatId, msg);
    }
}
