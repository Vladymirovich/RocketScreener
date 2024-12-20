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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminBotControllerTest {

    @Mock
    private Dotenv dotenv;

    @Mock
    private TemplateService templateService;

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private AdminBotController adminBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("ADMIN_BOT_TOKEN")).thenReturn("admin-test-token");
        when(dotenv.get("ADMIN_BOT_USERNAME")).thenReturn("admin-test-bot");
        when(dotenv.get("ADMIN_WHITELIST")).thenReturn("123456");
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
