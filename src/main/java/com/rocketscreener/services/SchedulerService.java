package com.rocketscreener.services;

import com.rocketscreener.controllers.PublicBotController;
import com.rocketscreener.templates.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * SchedulerService:
 * Handles scheduled tasks such as daily and weekly reports.
 */
@Service
public class SchedulerService {

    private final PublicBotController publicBot;
    private final TemplateService templateService;
    private final Dotenv dotenv;

    // Placeholder for public chat ID, replace with actual implementation to fetch dynamic chat IDs
    private final String publicChatId;

    public SchedulerService(PublicBotController publicBot, TemplateService templateService, Dotenv dotenv) {
        this.publicBot = publicBot;
        this.templateService = templateService;
        this.dotenv = dotenv;
        this.publicChatId = dotenv.get("PUBLIC_CHAT_ID", "PUBLIC_CHAT_ID_PLACEHOLDER");
    }

    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9:00
    public void dailyReport() {
        String lang = dotenv.get("DEFAULT_LANGUAGE", "en");
        String templateName = "daily_overview";
        publicBot.sendNotification(publicChatId, templateName, lang);
    }

    @Scheduled(cron = "0 0 20 ? * SUN") // Every Sunday at 20:00
    public void weeklyReport() {
        String lang = dotenv.get("DEFAULT_LANGUAGE", "en");
        String templateName = "weekly_overview";
        publicBot.sendNotification(publicChatId, templateName, lang);
    }
}
