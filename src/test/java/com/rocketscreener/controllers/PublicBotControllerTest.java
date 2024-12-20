package com.rocketscreener.controllers;

import com.rocketscreener.templates.TemplateService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        String text = "Test Notification";
        SendMessage sendMessage = new SendMessage(chatId, text);

        try {
            publicBotController.sendNotification(chatId, text);
            verify(templateService, never()).render(anyString(), anyString());
        } catch (Exception e) {
            fail("Exception thrown during sendNotification test: " + e.getMessage());
        }
    }

    @Test
    void testHandleUpdate() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(123L);

        publicBotController.onUpdateReceived(update);

        verify(templateService, never()).render(anyString(), anyString());
    }
}
