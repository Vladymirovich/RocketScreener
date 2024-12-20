package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.*;

class PublicBotControllerTest {

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private PublicBotController publicBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("PUBLIC_BOT_TOKEN")).thenReturn("dummy-public-token");
        when(dotenv.get("PUBLIC_BOT_USERNAME")).thenReturn("dummy-public-username");
        publicBotController = new PublicBotController(dotenv);
    }

    @Test
    void testHandleTextMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage().hasText()).thenReturn(true);
        when(update.getMessage().getText()).thenReturn("/start");
        when(update.getMessage().getChatId()).thenReturn(456L);

        publicBotController.onUpdateReceived(update);

        // Добавьте проверки вызова методов
        verifyNoMoreInteractions(dotenv);
    }
}
