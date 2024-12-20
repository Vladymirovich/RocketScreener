package com.rocketscreener.controllers;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.SourceRepository;
import com.rocketscreener.templates.TemplateService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

class AdminBotControllerTest {

    @Mock
    private Dotenv dotenv;

    @Mock
    private TemplateService templateService;

    @Mock
    private FilterRepository filterRepo;

    @Mock
    private SourceRepository sourceRepo;

    @InjectMocks
    private AdminBotController adminBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("ADMIN_BOT_TOKEN")).thenReturn("dummy-token");
        when(dotenv.get("ADMIN_BOT_USERNAME")).thenReturn("dummy-username");
        when(dotenv.get("ADMIN_WHITELIST")).thenReturn("12345,67890");
        adminBotController = new AdminBotController(dotenv);
    }

    @Test
    void testHandleTextMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/start");
        when(update.getMessage().getChatId()).thenReturn(123L);
        when(update.getMessage().getFrom().getId()).thenReturn(12345L);

        adminBotController.onUpdateReceived(update);

        verifyNoInteractions(templateService, filterRepo, sourceRepo);
    }

    // Добавьте другие тесты для проверки методов контроллера
}
