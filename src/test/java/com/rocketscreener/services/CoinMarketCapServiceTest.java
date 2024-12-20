package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CoinMarketCapServiceTest {

    @InjectMocks
    private CoinMarketCapService coinMarketCapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Dotenv dotenv = Dotenv.load();
        System.setProperty("COINMARKETCAP_API_KEY", dotenv.get("COINMARKETCAP_API_KEY")); // Load and set the test API key
    }

    @Test
    void testFetchCurrentMetrics() {
        // Mock behavior of CoinMarketCapService methods here and write test cases
        when(coinMarketCapService.fetchCurrentMetrics("price", Collections.singletonList("BTC"))).thenReturn(Collections.singletonMap("BTC", 50000.0));
        
        var metrics = coinMarketCapService.fetchCurrentMetrics("price", Collections.singletonList("BTC"));
        assertNotNull(metrics);
        assertEquals(50000.0, metrics.get("BTC"));
        verify(coinMarketCapService, times(1)).fetchCurrentMetrics("price", Collections.singletonList("BTC"));
    }
}
