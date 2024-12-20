package com.rocketscreener.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CoinMarketCapServiceTest {

    @Mock
    private CoinMarketCapApi coinMarketCapApi; // Мок для недостающего CoinMarketCapApi

    @InjectMocks
    private CoinMarketCapService coinMarketCapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    void testFetchCurrentMetrics() {
        String metric = "price";
        List<String> symbols = List.of("BTC", "ETH");

        // Мокируем API ответ
        when(coinMarketCapApi.fetchMetrics(metric, symbols))
                .thenReturn(Map.of("BTC", 30000.0, "ETH", 2000.0));

        Map<String, Double> result = coinMarketCapService.fetchCurrentMetrics(metric, symbols);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(30000.0, result.get("BTC"));
        assertEquals(2000.0, result.get("ETH"));

        verify(coinMarketCapApi, times(1)).fetchMetrics(metric, symbols);
    }

    @Test
    void testFetchChartUrl() {
        String symbol = "BTC";
        String expectedUrl = "https://coinmarketcap.com/currencies/btc/";

        String actualUrl = coinMarketCapService.fetchChartUrl(symbol);

        assertEquals(expectedUrl, actualUrl);
    }
}
