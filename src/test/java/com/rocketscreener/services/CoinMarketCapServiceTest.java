package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CoinMarketCapServiceTest {

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private CoinMarketCapService coinMarketCapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("COINMARKETCAP_API_KEY")).thenReturn("FAKE_CMC_KEY");
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
