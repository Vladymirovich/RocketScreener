package com.rocketscreener.controllers;

import com.rocketscreener.templates.TemplateService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PublicBotControllerTest {

    @Mock
    private Dotenv dotenv;

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private PublicBotController publicBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("PUBLIC_BOT_TOKEN")).thenReturn("test-token");
        when(dotenv.get("PUBLIC_BOT_USERNAME")).thenReturn("test-bot");
    }

    @Test
    void testSendNotification() {
        String chatId = "123456";
        String title = "Notification Title";
        String message = "Notification Message";

        publicBotController.sendNotification(chatId, title, message, new Object[]{"arg1", "arg2"});
        verify(templateService, never()).render(anyString(), anyString());
    }

    @Test
    void testHandleInvalidUpdate() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> publicBotController.onUpdateReceived(update));

        assertEquals("Invalid update received", exception.getMessage());
    }
}
