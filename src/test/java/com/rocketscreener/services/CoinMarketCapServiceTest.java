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

// Тесты для CoinMarketCapService
class CoinMarketCapServiceTest {

    @Mock
    private CoinMarketCapApi coinMarketCapApi; // Мок для CoinMarketCapApi

    @InjectMocks
    private CoinMarketCapService coinMarketCapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchCurrentMetrics() {
        String metric = "price";
        List<String> symbols = List.of("BTC", "ETH");

        // Мокируем возвращаемые данные от API
        when(coinMarketCapApi.fetchMetrics(metric, symbols)).thenReturn(Map.of("BTC", 50000.0, "ETH", 4000.0));

        Map<String, Double> result = coinMarketCapService.fetchCurrentMetrics(metric, symbols);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50000.0, result.get("BTC"));
        assertEquals(4000.0, result.get("ETH"));

        // Убеждаемся, что метод был вызван один раз
        verify(coinMarketCapApi, times(1)).fetchMetrics(metric, symbols);
    }

    @Test
    void testFetchChartUrl() {
        String symbol = "BTC";
        String expectedUrl = "https://coinmarketcap.com/currencies/btc/";

        String result = coinMarketCapService.fetchChartUrl(symbol);

        // Проверяем правильность возвращаемого URL
        assertEquals(expectedUrl, result);
    }
}
