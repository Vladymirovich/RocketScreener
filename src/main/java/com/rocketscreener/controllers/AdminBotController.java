package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AdminBotController.class);

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

        log.info("AdminBot received text message: {} from user: {}", text, userId);

        if(!isAdmin(userId)){
            sendText(chatId, "Access Denied: You are not in the admin whitelist.");
            return;
        }

        if(text.equals("/start")){
            sendText(chatId, "Welcome to the Admin Bot. Choose an option:");
            showMainMenu(chatId);
        } else if(text.startsWith("/add_filter")){
            // Format: /add_filter name metric threshold threshold_type interval
            // Example: /add_filter VolumeChange volume 10 percentage 5
            String[] parts = text.split(" ");
            if(parts.length == 6){
                String name = parts[1];
                String metric = parts[2];
                double threshold = Double.parseDouble(parts[3]);
                String thresholdType = parts[4];
                int interval = Integer.parseInt(parts[5]);
                filterRepo.addFilter(name, metric, threshold, thresholdType, interval, false, null);
                sendText(chatId, "Filter added successfully!");
            } else {
                sendText(chatId, "Usage: /add_filter name metric threshold threshold_type interval");
            }
        } else if(text.startsWith("/add_source")){
            // Format: /add_source name type base_url api_key priority
            // Example: /add_source CMC analytics https://pro-api.coinmarketcap.com YOUR_CMC_KEY 100
            String[] parts = text.split(" ");
            if(parts.length == 6){
                String name = parts[1];
                String type = parts[2];
                String baseUrl = parts[3];
                String apiKey = parts[4];
                int priority = Integer.parseInt(parts[5]);
                // We'll need a repository method for this:
                // In previous code, we have addSource(...) in SourceRepository.
                // Let's assume it's implemented.
                com.rocketscreener.storage.SourceRepository sr = getSourceRepo();
                sr.addSource(name, type, baseUrl, apiKey, priority);
                sendText(chatId, "Source added successfully!");
            } else {
                sendText(chatId, "Usage: /add_source name type base_url api_key priority");
            }
        } else {
            sendText(chatId, "Received admin command: " + text);
        }
    }

    private com.rocketscreener.storage.SourceRepository getSourceRepo(){
        // In a proper Spring setup we'd @Autowired it or get from context
        // For brevity, let's just assume we have a static context accessor
        return com.rocketscreener.utils.SpringContext.getBean(com.rocketscreener.storage.SourceRepository.class);
    }

    private void handleCallbackQuery(CallbackQuery query) {
        String chatId = query.getMessage().getChatId().toString();
        String userId = query.getFrom().getId().toString();
        log.info("AdminBot received callback: {} from user: {}", query.getData(), userId);

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
        answerCallbackQuery(callbackId,"Templates management coming soon...");
        // Similar approach as filters, we would list templates and provide edit options.
    }

    private void sendText(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        try{
            execute(msg);
        }catch(Exception e){
            log.error("Error sending message: ", e);
        }
    }

    private void sendInlineKeyboard(String chatId, String text, InlineKeyboardMarkup markup){
        SendMessage msg = new SendMessage(chatId, text);
        msg.setReplyMarkup(markup);
        try {
            execute(msg);
        } catch (Exception e) {
            log.error("Error sending inline keyboard: ", e);
        }
    }

    private void answerCallbackQuery(String callbackId, String text) {
        AnswerCallbackQuery acq = new AnswerCallbackQuery();
        acq.setCallbackQueryId(callbackId);
        acq.setText(text);
        try {
            execute(acq);
        } catch(Exception e){
            log.error("Error answering callback query: ", e);
        }
    }
}
