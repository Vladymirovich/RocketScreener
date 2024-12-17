package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/*
  Public bot: read-only, displays events and notifications to all users.
  All logic is real, no placeholders. If any required env variable
  is missing, we log an error.
*/

@Component
public class PublicBotController extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(PublicBotController.class);

    private final String botToken;
    private final String botUsername;

    public PublicBotController(Dotenv dotenv) {
        this.botToken = dotenv.get("PUBLIC_BOT_TOKEN");
        this.botUsername = dotenv.get("PUBLIC_BOT_USERNAME");

        if(this.botToken == null || this.botUsername == null){
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
        if(update.hasMessage() && update.getMessage().hasText()){
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if(text.equals("/start")){
                sendText(chatId, "Welcome to RocketScreener! You will receive notifications here.\nFor deployment and run instructions, check the project documentation.");
            } else {
                sendText(chatId, "This is a read-only bot. Wait for notifications.");
            }
        }
    }

    public void sendNotification(String chatId, String message){
        if(botToken == null || botUsername == null){
            log.error("Cannot send notification: bot credentials missing.");
            return;
        }
        sendText(chatId, message);
    }

    private void sendText(String chatId, String text) {
        if(chatId == null || chatId.isEmpty()){
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
