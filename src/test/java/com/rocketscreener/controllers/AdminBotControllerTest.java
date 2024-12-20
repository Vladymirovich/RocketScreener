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
        String adminMessage = "Update settings";
        String response = "Settings updated";

        when(adminService.handleAdminCommand(adminMessage)).thenReturn(response);

        String result = adminBotController.handleAdminUpdate(adminMessage);

        assertEquals(response, result);
        verify(adminService, times(1)).handleAdminCommand(adminMessage);
    }

    @Test
    void testUnauthorizedUser() {
        String unauthorizedMessage = "Unauthorized access";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> adminBotController.handleAdminUpdate(unauthorizedMessage));

        assertEquals("User is not authorized", exception.getMessage());
        verifyNoInteractions(adminService);
    }
}
