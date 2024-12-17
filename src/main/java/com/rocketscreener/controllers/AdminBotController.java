package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.templates.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdminBotController extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final List<String> adminWhitelist;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private FilterRepository filterRepo;

    public AdminBotController(Dotenv dotenv) {
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
            handleTextMessage(update);
        } else if(update.hasCallbackQuery()){
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleTextMessage(Update update){
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String userId = update.getMessage().getFrom().getId().toString();

        if(!isAdmin(userId)){
            sendText(chatId, "Access Denied: You are not in the admin whitelist.");
            return;
        }

        if(text.equals("/start")){
            sendText(chatId, "Welcome to the Admin Bot. Choose an option:");
            showMainMenu(chatId);
        } else {
            sendText(chatId, "Received admin command: " + text);
        }
    }

    private void handleCallbackQuery(CallbackQuery query) {
        String chatId = query.getMessage().getChatId().toString();
        String userId = query.getFrom().getId().toString();

        if(!isAdmin(userId)){
            answerCallbackQuery(query.getId(),"Access Denied");
            return;
        }

        String data = query.getData();
        if(data.equals("manage_filters")){
            showFiltersMenu(chatId, query.getId());
        } else if(data.equals("manage_templates")){
            showTemplatesMenu(chatId, query.getId());
        } else {
            // Handle other callbacks
            answerCallbackQuery(query.getId(),"Unknown action");
        }
    }

    private void showMainMenu(String chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Manage Filters").callbackData("manage_filters").build()));
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Manage Templates").callbackData("manage_templates").build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Admin Menu:", markup);
    }

    private void showFiltersMenu(String chatId, String callbackId) {
        List<FilterRecord> filters = filterRepo.findAllEnabled();
        if(filters.isEmpty()){
            answerCallbackQuery(callbackId,"No filters found");
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(FilterRecord f : filters){
            rows.add(Collections.singletonList(InlineKeyboardButton.builder().text(f.name()).callbackData("edit_filter:"+f.id()).build()));
        }
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Filters:", markup);
        answerCallbackQuery(callbackId,"Filters listed");
    }

    private void showTemplatesMenu(String chatId, String callbackId) {
        // Similar to filters menu, list templates and allow editing
        answerCallbackQuery(callbackId,"Templates management coming soon...");
    }

    private void sendText(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        try{
            execute(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendInlineKeyboard(String chatId, String text, InlineKeyboardMarkup markup){
        SendMessage msg = new SendMessage(chatId, text);
        msg.setReplyMarkup(markup);
        try {
            execute(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerCallbackQuery(String callbackId, String text) {
        AnswerCallbackQuery acq = new AnswerCallbackQuery();
        acq.setCallbackQueryId(callbackId);
        acq.setText(text);
        try {
            execute(acq);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
