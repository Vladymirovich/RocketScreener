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
class AdminBotControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminBotController adminBotController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleAdminUpdate() {
        String command = "update settings";
        String response = "Settings updated";

        when(adminService.handleCommand(command)).thenReturn(response);

        String result = adminBotController.handleAdminUpdate(command);

        assertEquals(response, result);
        verify(adminService, times(1)).handleCommand(command);
    }

    @Test
    void testUnauthorizedAccess() {
        String command = "restricted action";

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminBotController.handleAdminUpdate(command));

        assertEquals("Unauthorized access", exception.getMessage());
        verifyNoInteractions(adminService);
    }
}
