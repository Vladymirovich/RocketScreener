package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.SourceRepository;
import com.rocketscreener.storage.SourceRecord;
import com.rocketscreener.templates.TemplateService;

import java.util.*;

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
    @Autowired
    private SourceRepository sourceRepo;

    @Autowired
    public AdminBotController(Dotenv dotenv) {
        this.botToken = dotenv.get("ADMIN_BOT_TOKEN");
        this.botUsername = dotenv.get("ADMIN_BOT_USERNAME");
        String whitelist = dotenv.get("ADMIN_WHITELIST");
        if (whitelist != null && !whitelist.isEmpty()) {
            this.adminWhitelist = Arrays.asList(whitelist.split(","));
        } else {
            this.adminWhitelist = new ArrayList<>();
            log.warn("ADMIN_WHITELIST is empty or not set in .env");
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
            sendText(chatId, "Welcome to Admin Bot menu. Use the inline menu.");
            showMainMenu(chatId);
        } else if(text.startsWith("/add_filter")){
            // Format: /add_filter name metric threshold threshold_type interval
            // Example: /add_filter VolumeChange volume 10 percentage 5
            String[] parts = text.split(" ");
            if(parts.length == 6){
                try{
                    String name = parts[1];
                    String metric = parts[2];
                    double threshold = Double.parseDouble(parts[3]);
                    String thresholdType = parts[4];
                    int interval = Integer.parseInt(parts[5]);
                    filterRepo.addFilter(name, metric, threshold, thresholdType, interval, false, null);
                    sendText(chatId, "Filter added successfully!");
                } catch(NumberFormatException e){
                    sendText(chatId, "Invalid number format. Please check your command.");
                    log.error("Error parsing numbers in /add_filter command", e);
                } catch(Exception e){
                    sendText(chatId, "Failed to add filter. Please try again.");
                    log.error("Error adding filter", e);
                }
            } else {
                sendText(chatId, "Usage: /add_filter name metric threshold threshold_type interval");
            }
        } else if(text.startsWith("/add_source")){
            // Format: /add_source name type base_url api_key priority
            // Example: /add_source CMC analytics https://pro-api.coinmarketcap.com YOUR_CMC_KEY 100
            String[] parts = text.split(" ");
            if(parts.length == 6){
                try{
                    String name = parts[1];
                    String type = parts[2];
                    String baseUrl = parts[3];
                    String apiKey = parts[4];
                    int priority = Integer.parseInt(parts[5]);
                    sourceRepo.addSource(name, type, baseUrl, apiKey, priority);
                    sendText(chatId, "Source added successfully!");
                } catch(NumberFormatException e){
                    sendText(chatId, "Invalid number format. Please check your command.");
                    log.error("Error parsing numbers in /add_source command", e);
                } catch(Exception e){
                    sendText(chatId, "Failed to add source. Please try again.");
                    log.error("Error adding source", e);
                }
            } else {
                sendText(chatId, "Usage: /add_source name type base_url api_key priority");
            }
        } else {
            sendText(chatId, "Use the inline menus or /start to see options.");
        }
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
        } else if(data.equals("manage_sources")){
            showSourcesMenu(chatId, query.getId());
        } else if(data.startsWith("edit_filter:")){
            handleEditFilter(chatId, query.getId(), data);
        } else if(data.startsWith("edit_source:")){
            handleEditSource(chatId, query.getId(), data);
        } else if(data.equals("add_filter")){
            promptAddFilter(chatId);
            answerCallbackQuery(query.getId(),"Enter filter details in chat.");
        } else if(data.equals("add_source")){
            promptAddSource(chatId);
            answerCallbackQuery(query.getId(),"Enter source details in chat.");
        } else {
            answerCallbackQuery(query.getId(),"Unknown action");
        }
    }

    private void promptAddFilter(String chatId) {
        sendText(chatId, "Please add filter:\n`/add_filter name metric threshold threshold_type interval`");
    }

    private void promptAddSource(String chatId){
        sendText(chatId, "Please add source:\n`/add_source name type base_url api_key priority`");
    }

    private void showMainMenu(String chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Manage Filters").callbackData("manage_filters").build()));
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Manage Sources").callbackData("manage_sources").build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Admin Menu:", markup);
    }

    private void showFiltersMenu(String chatId, String callbackId) {
        List<FilterRecord> filters = filterRepo.findAllEnabled();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(FilterRecord f : filters){
            rows.add(Collections.singletonList(InlineKeyboardButton.builder().text(f.name()).callbackData("edit_filter:"+f.id()).build()));
        }
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Add Filter").callbackData("add_filter").build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Filters:", markup);
        answerCallbackQuery(callbackId,"Filters listed");
    }

    private void handleEditFilter(String chatId, String callbackId, String data){
        String[] parts = data.split(":");
        if(parts.length==2){
            try{
                int filterId = Integer.parseInt(parts[1]);
                FilterRecord f = filterRepo.findAllEnabled().stream().filter(x->x.id()==filterId).findFirst().orElse(null);
                if(f!=null){
                    sendText(chatId, "Filter details:\nName: "+f.name()+"\nMetric: "+f.metric()+"\nThreshold: "+f.thresholdValue()+"\nInterval: "+f.timeIntervalMinutes()+" min");
                    answerCallbackQuery(callbackId,"Filter details shown");
                } else {
                    answerCallbackQuery(callbackId,"Filter not found");
                }
            } catch(NumberFormatException e){
                sendText(chatId, "Invalid filter ID format.");
                log.error("Error parsing filter ID", e);
                answerCallbackQuery(callbackId,"Invalid filter ID format");
            }
        } else {
            answerCallbackQuery(callbackId,"Invalid data format");
        }
    }

    private void showSourcesMenu(String chatId, String callbackId){
        List<SourceRecord> sources = sourceRepo.findAllEnabledSources();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(SourceRecord s : sources){
            rows.add(Collections.singletonList(InlineKeyboardButton.builder().text(s.name()).callbackData("edit_source:"+s.id()).build()));
        }
        rows.add(Collections.singletonList(InlineKeyboardButton.builder().text("Add Source").callbackData("add_source").build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Sources:", markup);
        answerCallbackQuery(callbackId,"Sources listed");
    }

    private void handleEditSource(String chatId, String callbackId, String data){
        String[] parts = data.split(":");
        if(parts.length==2){
            try{
                int sourceId = Integer.parseInt(parts[1]);
                SourceRecord s = sourceRepo.findAllEnabledSources().stream().filter(x->x.id()==sourceId).findFirst().orElse(null);
                if(s!=null){
                    sendText(chatId, "Source details:\nName: "+s.name()+"\nType: "+s.type()+"\nBase URL: "+s.baseUrl()+"\nPriority: "+s.priority());
                    answerCallbackQuery(callbackId,"Source details shown");
                } else {
                    answerCallbackQuery(callbackId,"Source not found");
                }
            } catch(NumberFormatException e){
                sendText(chatId, "Invalid source ID format.");
                log.error("Error parsing source ID", e);
                answerCallbackQuery(callbackId,"Invalid source ID format");
            }
        } else {
            answerCallbackQuery(callbackId,"Invalid data format");
        }
    }

    private void sendText(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        msg.enableMarkdown(true);
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
