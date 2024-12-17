package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminBotController extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final List<String> adminWhitelist;

    public AdminBotController(io.github.cdimascio.dotenv.Dotenv dotenv) {
        this.botToken = dotenv.get("ADMIN_BOT_TOKEN");
        this.botUsername = dotenv.get("ADMIN_BOT_USERNAME");
        this.adminWhitelist = Arrays.asList(dotenv.get("ADMIN_WHITELIST").split(","));
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    private boolean isAdmin(String userId) {
        return adminWhitelist.contains(userId);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String userId = update.getMessage().getFrom().getId().toString();

            if(!isAdmin(userId)){
                sendText(chatId, "Access Denied: You are not in the admin whitelist.");
                return;
            }

            // Basic command handling
            if(text.equals("/start")){
                sendText(chatId, "Welcome to the Admin Bot. Use the inline menu to configure settings.");
                // Here we will later add inline keyboards for settings.
            } else {
                // Process admin commands, e.g. changing filters, sources, templates.
                // We will implement full logic as we go along.
                sendText(chatId, "Received admin command: " + text);
            }
        }
    }

    private void sendText(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        try{
            execute(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
