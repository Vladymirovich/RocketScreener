package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

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
        when(coinMarketCapService.fetchCurrentMetrics("BTC")).thenReturn(new Metrics());
        
        Metrics metrics = coinMarketCapService.fetchCurrentMetrics("BTC");
        assertNotNull(metrics);
        verify(coinMarketCapService, times(1)).fetchCurrentMetrics("BTC");
    }
}
