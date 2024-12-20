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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        String title = "Test Title";
        String message = "Test Message";

        publicBotController.sendNotification(chatId, title, message, new Object[]{});
        verify(templateService, never()).render(anyString(), anyString());
    }

    @Test
    void testHandleUpdateWithInvalidMessage() {
        Update update = mock(Update.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> publicBotController.onUpdateReceived(update));
    }
}
