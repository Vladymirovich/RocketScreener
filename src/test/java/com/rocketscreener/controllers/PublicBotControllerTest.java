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
        String expectedResponse = "Hi, how can I assist you?";

        when(botService.processMessage(message)).thenReturn(expectedResponse);

        String result = publicBotController.handleUpdate(message);

        assertEquals(expectedResponse, result);
        verify(botService, times(1)).processMessage(message);
    }

    @Test
    void testHandleInvalidUpdate() {
        String invalidMessage = "";

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> publicBotController.handleUpdate(invalidMessage));

        assertEquals("Invalid update message", exception.getMessage());
        verifyNoInteractions(botService);
    }
}
