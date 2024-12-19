package com.rocketscreener.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import com.rocketscreener.templates.TemplateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PublicBotControllerTest {

    @MockBean
    private Dotenv dotenv;

    @MockBean
    private TemplateService templateService;

    @Test
    void testOnUpdateReceived() {
        // Mock dotenv variables
        Mockito.when(dotenv.get("PUBLIC_BOT_TOKEN")).thenReturn("dummy_token");
        Mockito.when(dotenv.get("PUBLIC_BOT_USERNAME")).thenReturn("dummy_username");

        // Mock TemplateService response
        Mockito.when(templateService.generateResponse("Hello")).thenReturn("You said: Hello");

        // Create PublicBotController instance
        PublicBotController publicBot = new PublicBotController(dotenv, templateService);

        // Create a mock Update object
        Update update = new Update();
        org.telegram.telegrambots.meta.api.objects.Message message = new org.telegram.telegrambots.meta.api.objects.Message();
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User();
        user.setId(123456789L);
        message.setFrom(user);
        message.setChat(new org.telegram.telegrambots.meta.api.objects.Chat());
        message.setText("Hello");
        update.setMessage(message);

        // Invoke onUpdateReceived
        publicBot.onUpdateReceived(update);

        // Verify that templateService.generateResponse was called
        verify(templateService, times(1)).generateResponse("Hello");
    }
}
