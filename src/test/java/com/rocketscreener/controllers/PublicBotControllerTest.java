package com.rocketscreener.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PublicBotControllerTest {

    @Mock
    private BotService botService;

    @InjectMocks
    private PublicBotController publicBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleValidUpdate() {
        String message = "Hello Bot";
        String response = "Hi, how can I help you?";

        when(botService.handleMessage(message)).thenReturn(response);

        String result = publicBotController.handleUpdate(message);

        assertEquals(response, result);
        verify(botService, times(1)).handleMessage(message);
    }

    @Test
    void testHandleInvalidUpdate() {
        String invalidMessage = "";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> publicBotController.handleUpdate(invalidMessage));

        assertEquals("Invalid update", exception.getMessage());
        verifyNoInteractions(botService);
    }
}
