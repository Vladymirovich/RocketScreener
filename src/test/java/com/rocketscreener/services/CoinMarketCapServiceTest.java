package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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
        when(dotenv.get("COINMARKETCAP_API_KEY")).thenReturn("test-api-key");
    }

    @Test
    void testFetchCurrentMetrics() throws IOException, InterruptedException {
        // Mocking the HTTP client and request responses
        HttpClient mockHttpClient = mock(HttpClient.class);
        coinMarketCapService.setHttpClient(mockHttpClient);

        String metric = "price";
        List<String> symbols = List.of("BTC", "ETH");
        String responseBody = """
            {
                "data": {
                    "BTC": {
                        "quote": {
                            "USD": {
                                "price": 50000.0,
                                "volume_24h": 1000000000.0
                            }
                        }
                    },
                    "ETH": {
                        "quote": {
                            "USD": {
                                "price": 4000.0,
                                "volume_24h": 500000000.0
                            }
                        }
                    }
                }
            }
        """;

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(responseBody);

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Map<String, Double> result = coinMarketCapService.fetchCurrentMetrics(metric, symbols);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50000.0, result.get("BTC"));
        assertEquals(4000.0, result.get("ETH"));
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testFetchChartUrl() {
        String symbol = "BTC";
        String expectedUrl = "https://coinmarketcap.com/currencies/btc/";

        String result = coinMarketCapService.fetchChartUrl(symbol);

        assertEquals(expectedUrl, result);
    }
}
