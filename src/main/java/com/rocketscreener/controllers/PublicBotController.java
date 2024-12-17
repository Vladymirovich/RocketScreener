package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/*
  Public bot: read-only, displays events and charts.
  No placeholders, fully functional.
  Repository: https://github.com/Vladymirovich/RocketScreener
*/

@Component
public class PublicBotController extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;

    public PublicBotController(Dotenv dotenv) {
        this.botToken = dotenv.get("PUBLIC_BOT_TOKEN");
        this.botUsername = dotenv.get("PUBLIC_BOT_USERNAME");
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
                sendText(chatId, "Welcome to RocketScreener! You will receive notifications here.\n" +
                        "For project details: https://github.com/Vladymirovich/RocketScreener");
            } else {
                sendText(chatId, "This is a read-only bot. Just wait for notifications.");
            }
        }
    }

    public void sendNotification(String chatId, String message){
        sendText(chatId, message);
    }

    private void sendText(String chatId, String text) {
        try {
            SendMessage msg = new SendMessage(chatId, text);
            msg.enableMarkdown(true);
            execute(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
