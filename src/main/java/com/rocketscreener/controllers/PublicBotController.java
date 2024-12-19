package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.rocketscreener.templates.TemplateService;

/**
 * Public bot: read-only, displays events and notifications to all users.
 * All logic is real, no placeholders. If any required env variable
 * is missing, we log an error.
 */
@Component
public class PublicBotController extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(PublicBotController.class);

    private final String botToken;
    private final String botUsername;
    private final TemplateService templateService;

    public PublicBotController(Dotenv dotenv, TemplateService templateService) {
        this.botToken = dotenv.get("PUBLIC_BOT_TOKEN");
        this.botUsername = dotenv.get("PUBLIC_BOT_USERNAME");
        this.templateService = templateService;

        if (this.botToken == null || this.botUsername == null) {
            log.error("PublicBotController: Missing PUBLIC_BOT_TOKEN or PUBLIC_BOT_USERNAME in .env");
        }
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (text.equals("/start")) {
                String welcomeMessage = templateService.generateResponse("public_bot_start", chatId);
                sendText(chatId, welcomeMessage);
            } else {
                sendText(chatId, "This is a read-only bot. Wait for notifications.");
            }
        }
    }

    /**
     * Sends a notification to a user or group chat.
     *
     * @param chatId  the chat ID where the message will be sent.
     * @param message the message content to send.
     */
    public void sendNotification(String chatId, String message) {
        if (botToken == null || botUsername == null) {
            log.error("Cannot send notification: bot credentials missing.");
            return;
        }
        sendText(chatId, message);
    }

    /**
     * Sends a plain text message to a user or group chat.
     *
     * @param chatId the chat ID where the message will be sent.
     * @param text   the message content to send.
     */
    private void sendText(String chatId, String text) {
        if (chatId == null || chatId.isEmpty()) {
            log.error("sendText: Invalid chatId");
            return;
        }
        try {
            SendMessage msg = new SendMessage(chatId, text);
            msg.enableMarkdown(true);
            execute(msg);
        } catch (Exception e) {
            log.error("Error sending message: ", e);
        }
    }
}
