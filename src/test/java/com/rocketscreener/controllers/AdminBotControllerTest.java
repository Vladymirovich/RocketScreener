package com.rocketscreener.controllers;

import com.rocketscreener.storage.FilterRepository;
import com.rocketscreener.storage.SourceRepository;
import com.rocketscreener.templates.TemplateService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminBotControllerTest {

    @InjectMocks
    private AdminBotController adminBotController;

    @Mock
    private TemplateService templateService;

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private SourceRepository sourceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Dotenv dotenv = Dotenv.load();
        System.setProperty("ADMIN_BOT_TOKEN", dotenv.get("ADMIN_BOT_TOKEN")); // Load and set the test admin bot token
        System.setProperty("ADMIN_BOT_USERNAME", dotenv.get("ADMIN_BOT_USERNAME")); // Load and set the test admin bot username
        System.setProperty("ADMIN_WHITELIST", dotenv.get("ADMIN_WHITELIST")); // Load and set the test admin whitelist
    }

    @Test
    void testHandleAdminUpdate() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(123L);
        when(message.getFrom().getId()).thenReturn(123456L);

        adminBotController.onUpdateReceived(update);

        verify(templateService, never()).render(anyString(), anyString());
    }

    @Test
    void testUnauthorizedUser() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(123L);
        when(message.getFrom().getId()).thenReturn(999999L);

        Exception exception = assertThrows(SecurityException.class, () -> adminBotController.onUpdateReceived(update));

        assertEquals("User not authorized", exception.getMessage());
    }
}
