package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.rocketscreener.templates.TemplateService;

import static org.mockito.Mockito.*;

class PublicBotControllerTest {

    @Mock
    private Dotenv dotenv;

    @Mock
    private TemplateService templateService;

    private PublicBotController publicBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("PUBLIC_BOT_TOKEN")).thenReturn("dummyToken");
        when(dotenv.get("PUBLIC_BOT_USERNAME")).thenReturn("dummyUsername");
        publicBotController = new PublicBotController(dotenv, templateService);
    }

    @Test
    void testSendNotification() {
        String chatId = "123456";
        String templateName = "testTemplate";
        String language = "en";
        Object[] args = {"arg1", "arg2"};

        when(templateService.render(templateName, language, args)).thenReturn("Test message");
        publicBotController.sendNotification(chatId, templateName, language, args);

        verify(templateService).render(templateName, language, args);
    }
}
