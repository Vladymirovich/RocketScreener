package com.rocketscreener.filters;

import com.rocketscreener.services.FilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompositeFilterEvaluatorTest {

    @Mock
    private FilterService filterService;

    @InjectMocks
    private CompositeFilterEvaluator compositeFilterEvaluator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEvaluateSimpleOr() {
        String filterExpression = "price > 100 OR volume > 1000";
        String asset = "BTC";
        double price = 150.0;

        when(filterService.evaluate(filterExpression, asset, "price", price)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset, "price", price);

        assertTrue(result, "Evaluation failed for simple OR expression.");
    }

    @Test
    void testEvaluateSimpleAnd() {
        String filterExpression = "price > 100 AND volume > 1000";
        String asset = "ETH";
        double volume = 1200.0;

        when(filterService.evaluate(filterExpression, asset, "volume", volume)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset, "volume", volume);

        assertTrue(result, "Evaluation failed for simple AND expression.");
    }
}
