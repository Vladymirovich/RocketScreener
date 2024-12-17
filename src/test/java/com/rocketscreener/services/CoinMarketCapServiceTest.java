package com.rocketscreener.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;

public class CoinMarketCapServiceTest {

    @Mock
    private Dotenv dotenv;

    @InjectMocks
    private CoinMarketCapService coinMarketCapService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dotenv.get("COINMARKETCAP_API_KEY")).thenReturn("FAKE_CMC_KEY");
    }

    @Test
    public void testFetchCurrentMetrics() {
        // Since CoinMarketCapService likely makes HTTP requests, you would mock HTTP responses here.
        // For simplicity, assuming the method returns a fake value.

        Map<String, Double> metrics = coinMarketCapService.fetchCurrentMetrics("price", Collections.singletonList("BTC"));
        // Replace with actual expected behavior
        assertNotNull(metrics);
        // Example assertion (adjust based on actual implementation)
        // assertEquals(50000.0, metrics.get("BTC"));
    }
}
