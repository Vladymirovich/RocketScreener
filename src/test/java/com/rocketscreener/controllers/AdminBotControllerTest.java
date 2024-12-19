package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.SourceRepository;
import com.rocketscreener.templates.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminBotControllerTest {

    @MockBean
    private Dotenv dotenv;

    @MockBean
    private TemplateService templateService;

    @MockBean
    private FilterRepository filterRepo;

    @MockBean
    private SourceRepository sourceRepo;

    private AdminBotController adminBot;

    @BeforeEach
    void setUp() {
        Mockito.when(dotenv.get("ADMIN_BOT_TOKEN")).thenReturn("dummy_admin_token");
        Mockito.when(dotenv.get("ADMIN_BOT_USERNAME")).thenReturn("dummy_admin_username");
        Mockito.when(dotenv.get("ADMIN_WHITELIST")).thenReturn("123456789");

        adminBot = new AdminBotController(dotenv);
        adminBot.templateService = templateService;
        adminBot.filterRepo = filterRepo;
        adminBot.sourceRepo = sourceRepo;
    }

    @Test
    void testOnUpdateReceived_AccessDenied() {
        // Create a mock Update object with non-admin user
        Update update = new Update();
        org.telegram.telegrambots.meta.api.objects.Message message = new org.telegram.telegrambots.meta.api.objects.Message();
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(987654321L); // Not in whitelist
        message.setFrom(user);
        message.setChat(new org.telegram.telegrambots.meta.api.objects.Chat());
        message.setText("/start");
        update.setMessage(message);

        // Invoke onUpdateReceived
        adminBot.onUpdateReceived(update);

        // Verify that no menu was shown
        verify(sourceRepo, never()).findAllEnabledSources();
    }

    @Test
    void testOnUpdateReceived_AddFilter() {
        // Mock whitelist user
        Update update = new Update();
        org.telegram.telegrambots.meta.api.objects.Message message = new org.telegram.telegrambots.meta.api.objects.Message();
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(123456789L); // In whitelist
        message.setFrom(user);
        message.setChat(new org.telegram.telegrambots.meta.api.objects.Chat());
        message.setText("/add_filter VolumeChange volume 10 greater_than 5 false \"\"");
        update.setMessage(message);

        // Invoke onUpdateReceived
        adminBot.onUpdateReceived(update);

        // Verify that addFilter was called
        verify(filterRepo, times(1)).addFilter("VolumeChange", "volume", new java.math.BigDecimal("10"), "greater_than", 5, false, "");
    }

    @Test
    void testOnUpdateReceived_AddSource_InvalidFormat() {
        // Mock whitelist user
        Update update = new Update();
        org.telegram.telegrambots.meta.api.objects.Message message = new org.telegram.telegrambots.meta.api.objects.Message();
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(123456789L); // In whitelist
        message.setFrom(user);
        message.setChat(new org.telegram.telegrambots.meta.api.objects.Chat());
        message.setText("/add_source CMC analytics https://pro-api.coinmarketcap.com YOUR_CMC_KEY"); // Missing priority
        update.setMessage(message);

        // Invoke onUpdateReceived
        adminBot.onUpdateReceived(update);

        // Verify that addSource was not called
        verify(sourceRepo, never()).addSource(any(), any(), any(), any(), anyInt());
    }

    // Add additional tests as needed
}
