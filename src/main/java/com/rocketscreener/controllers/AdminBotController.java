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
import com.rocketscreener.storage.SourceRepository;
import com.rocketscreener.templates.TemplateService;

import java.math.BigDecimal;
import java.util.*;

/**
 * AdminBotController:
 * Handles administrative commands and interactions for the RocketScreener Telegram bot.
 */
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

    
    /**
     * Provides access to `templateService` for testing purposes.
     */
    public TemplateService getTemplateService() {
        return this.templateService;
    }

    /**
     * Provides access to `filterRepo` for testing purposes.
     */
    public FilterRepository getFilterRepo() {
        return this.filterRepo;
    }

    /**
     * Provides access to `sourceRepo` for testing purposes.
     */
    public SourceRepository getSourceRepo() {
        return this.sourceRepo;
    }
    
    /**
     * Checks if the user is an admin based on the whitelist.
     *
     * @param userId The Telegram user ID.
     * @return true if the user is an admin, else false.
     */
    private boolean isAdmin(String userId) {
        return adminWhitelist.contains(userId);
    }

  @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    /**
     * Handles incoming text messages.
     *
     * @param update The update containing the message.
     */
    private void handleTextMessage(Update update) {
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String userId = update.getMessage().getFrom().getId().toString();

        log.info("AdminBot received text message: {} from user: {}", text, userId);

        if (!isAdmin(userId)) {
            sendText(chatId, "Access Denied: You are not in the admin whitelist.");
            return;
        }

        if (text.equals("/start")) {
            sendText(chatId, "Welcome to Admin Bot menu. Use the inline menu.");
            showMainMenu(chatId);
        } else if (text.startsWith("/add_filter")) {
            // Format: /add_filter name metric threshold threshold_type timeIntervalMinutes isComposite compositeExpression
            // Example: /add_filter VolumeChange volume 10 greater_than 5 false ""
            String[] parts = text.split(" ", 9);
            if (parts.length == 9) {
                try {
                    String name = parts[1];
                    String metric = parts[2];
                    BigDecimal thresholdValue = new BigDecimal(parts[3]);
                    String thresholdType = parts[4];
                    int timeIntervalMinutes = Integer.parseInt(parts[5]);
                    boolean isComposite = Boolean.parseBoolean(parts[6]);
                    String compositeExpression = parts[7].equals("\"\"") ? "" : parts[7];
                    // If compositeExpression contains spaces, parts[8] should be appended
                    if (!parts[8].equals("\"\"") && !parts[8].isBlank()) {
                        compositeExpression += " " + parts[8];
                    }
                    filterRepo.addFilter(name, metric, thresholdValue, thresholdType, timeIntervalMinutes, isComposite, compositeExpression);
                    sendText(chatId, "Filter added successfully!");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Invalid number format. Please check your command.");
                    log.error("Error parsing numbers in /add_filter command", e);
                } catch (Exception e) {
                    sendText(chatId, "Failed to add filter. Please try again.");
                    log.error("Error adding filter", e);
                }
            } else {
                sendText(chatId, "Usage: /add_filter name metric threshold threshold_type timeIntervalMinutes isComposite compositeExpression");
            }
        } else if (text.startsWith("/add_source")) {
            // Format: /add_source name type base_url api_key priority
            // Example: /add_source CMC analytics https://pro-api.coinmarketcap.com YOUR_CMC_KEY 100
            String[] parts = text.split(" ", 6);
            if (parts.length == 6) {
                try {
                    String name = parts[1];
                    String type = parts[2];
                    String baseUrl = parts[3];
                    String apiKey = parts[4];
                    int priority = Integer.parseInt(parts[5]);
                    sourceRepo.addSource(name, type, baseUrl, apiKey, priority);
                    sendText(chatId, "Source added successfully!");
                } catch (NumberFormatException e) {
                    sendText(chatId, "Invalid number format. Please check your command.");
                    log.error("Error parsing numbers in /add_source command", e);
                } catch (Exception e) {
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

    /**
     * Handles incoming callback queries from inline keyboards.
     *
     * @param query The callback query.
     */
    private void handleCallbackQuery(CallbackQuery query) {
        String chatId = query.getMessage().getChatId().toString();
        String userId = query.getFrom().getId().toString();
        log.info("AdminBot received callback: {} from user: {}", query.getData(), userId);

        if (!isAdmin(userId)) {
            answerCallbackQuery(query.getId(), "Access Denied");
            return;
        }

        String data = query.getData();
        if (data.equals("manage_filters")) {
            showFiltersMenu(chatId, query.getId());
        } else if (data.equals("manage_sources")) {
            showSourcesMenu(chatId, query.getId());
        } else if (data.startsWith("edit_filter:")) {
            handleEditFilter(chatId, query.getId(), data);
        } else if (data.startsWith("edit_source:")) {
            handleEditSource(chatId, query.getId(), data);
        } else if (data.equals("add_filter")) {
            promptAddFilter(chatId);
            answerCallbackQuery(query.getId(), "Enter filter details in chat.");
        } else if (data.equals("add_source")) {
            promptAddSource(chatId);
            answerCallbackQuery(query.getId(), "Enter source details in chat.");
        } else {
            answerCallbackQuery(query.getId(), "Unknown action");
        }
    }

    /**
     * Prompts the admin to add a new filter via command.
     *
     * @param chatId The chat ID.
     */
    private void promptAddFilter(String chatId) {
        sendText(chatId, "Please add filter:\n`/add_filter name metric threshold threshold_type timeIntervalMinutes isComposite compositeExpression`");
    }

    /**
     * Prompts the admin to add a new source via command.
     *
     * @param chatId The chat ID.
     */
    private void promptAddSource(String chatId) {
        sendText(chatId, "Please add source:\n`/add_source name type base_url api_key priority`");
    }

    /**
     * Displays the main admin menu with options to manage filters and sources.
     *
     * @param chatId The chat ID.
     */
    private void showMainMenu(String chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Manage Filters")
                        .callbackData("manage_filters")
                        .build()));
        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Manage Sources")
                        .callbackData("manage_sources")
                        .build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Admin Menu:", markup);
    }

    /**
     * Displays the filters management menu with options to edit or add filters.
     *
     * @param chatId     The chat ID.
     * @param callbackId The callback query ID.
     */
    private void showFiltersMenu(String chatId, String callbackId) {
        List<FilterRecord> filters = filterRepo.findAllEnabled();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (FilterRecord f : filters) {
            rows.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(f.name())
                            .callbackData("edit_filter:" + f.id())
                            .build()));
        }
        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Add Filter")
                        .callbackData("add_filter")
                        .build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Filters:", markup);
        answerCallbackQuery(callbackId, "Filters listed");
    }

    /**
     * Handles the editing of a specific filter based on its ID.
     *
     * @param chatId     The chat ID.
     * @param callbackId The callback query ID.
     * @param data       The callback data containing the filter ID.
     */
    private void handleEditFilter(String chatId, String callbackId, String data) {
        String[] parts = data.split(":");
        if (parts.length == 2) {
            try {
                int filterId = Integer.parseInt(parts[1]);
                FilterRecord f = filterRepo.findAllEnabled().stream().filter(x -> x.id() == filterId).findFirst().orElse(null);
                if (f != null) {
                    StringBuilder details = new StringBuilder();
                    details.append("Filter details:\n")
                           .append("Name: ").append(f.name()).append("\n")
                           .append("Metric: ").append(f.metric()).append("\n")
                           .append("Threshold: ").append(f.thresholdValue()).append("\n")
                           .append("Threshold Type: ").append(f.thresholdType()).append("\n")
                           .append("Interval: ").append(f.timeIntervalMinutes()).append(" min").append("\n")
                           .append("Is Composite: ").append(f.isComposite()).append("\n")
                           .append("Composite Expression: ").append(f.compositeExpression());
                    sendText(chatId, details.toString());
                    answerCallbackQuery(callbackId, "Filter details shown");
                } else {
                    answerCallbackQuery(callbackId, "Filter not found");
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "Invalid filter ID format.");
                log.error("Error parsing filter ID", e);
                answerCallbackQuery(callbackId, "Invalid filter ID format");
            }
        } else {
            answerCallbackQuery(callbackId, "Invalid data format");
        }
    }

    /**
     * Displays the sources management menu with options to edit or add sources.
     *
     * @param chatId     The chat ID.
     * @param callbackId The callback query ID.
     */
    private void showSourcesMenu(String chatId, String callbackId) {
        List<SourceRecord> sources = sourceRepo.findAllEnabledSources();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (SourceRecord s : sources) {
            rows.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(s.name())
                            .callbackData("edit_source:" + s.id())
                            .build()));
        }
        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Add Source")
                        .callbackData("add_source")
                        .build()));
        markup.setKeyboard(rows);

        sendInlineKeyboard(chatId, "Sources:", markup);
        answerCallbackQuery(callbackId, "Sources listed");
    }

    /**
     * Handles the editing of a specific source based on its ID.
     *
     * @param chatId     The chat ID.
     * @param callbackId The callback query ID.
     * @param data       The callback data containing the source ID.
     */
    private void handleEditSource(String chatId, String callbackId, String data) {
        String[] parts = data.split(":");
        if (parts.length == 2) {
            try {
                int sourceId = Integer.parseInt(parts[1]);
                SourceRecord s = sourceRepo.findAllEnabledSources().stream().filter(x -> x.id() == sourceId).findFirst().orElse(null);
                if (s != null) {
                    StringBuilder details = new StringBuilder();
                    details.append("Source details:\n")
                           .append("Name: ").append(s.name()).append("\n")
                           .append("Type: ").append(s.type()).append("\n")
                           .append("Base URL: ").append(s.baseUrl()).append("\n")
                           .append("API Key: ").append(s.apiKey()).append("\n")
                           .append("Priority: ").append(s.priority());
                    sendText(chatId, details.toString());
                    answerCallbackQuery(callbackId, "Source details shown");
                } else {
                    answerCallbackQuery(callbackId, "Source not found");
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "Invalid source ID format.");
                log.error("Error parsing source ID", e);
                answerCallbackQuery(callbackId, "Invalid source ID format");
            }
        } else {
            answerCallbackQuery(callbackId, "Invalid data format");
        }
    }

    /**
     * Sends a plain text message to the specified chat.
     *
     * @param chatId The chat ID.
     * @param text   The text message.
     */
    private void sendText(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        msg.enableMarkdown(true);
        try {
            execute(msg);
        } catch (Exception e) {
            log.error("Error sending message: ", e);
        }
    }

    /**
     * Sends an inline keyboard message to the specified chat.
     *
     * @param chatId The chat ID.
     * @param text   The text message.
     * @param markup The inline keyboard markup.
     */
    private void sendInlineKeyboard(String chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage(chatId, text);
        msg.setReplyMarkup(markup);
        try {
            execute(msg);
        } catch (Exception e) {
            log.error("Error sending inline keyboard: ", e);
        }
    }

    /**
     * Answers a callback query with the specified text.
     *
     * @param callbackId The callback query ID.
     * @param text       The text to send as a response.
     */
    private void answerCallbackQuery(String callbackId, String text) {
        AnswerCallbackQuery acq = new AnswerCallbackQuery();
        acq.setCallbackQueryId(callbackId);
        acq.setText(text);
        try {
            execute(acq);
        } catch (Exception e) {
            log.error("Error answering callback query: ", e);
        }
    }
}
