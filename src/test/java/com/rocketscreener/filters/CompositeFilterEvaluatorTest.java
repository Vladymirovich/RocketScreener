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
        String field = "price";
        double value = 150.0;

        when(filterService.evaluate(filterExpression, asset, field, value)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset, field, value);

        assertTrue(result, "Evaluation failed for simple OR expression.");
    }

    @Test
    void testEvaluateSimpleAnd() {
        String filterExpression = "price > 100 AND volume > 1000";
        String asset = "ETH";
        String field = "volume";
        double value = 1200.0;

        when(filterService.evaluate(filterExpression, asset, field, value)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset, field, value);

        assertTrue(result, "Evaluation failed for simple AND expression.");
    }

    @Test
    void testEvaluateInvalidExpression() {
        String filterExpression = "INVALID_EXPRESSION";
        String asset = "BTC";
        String field = "price";
        double value = 100.0;

        when(filterService.evaluate(filterExpression, asset, field, value)).thenThrow(new IllegalArgumentException("Invalid filter expression"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compositeFilterEvaluator.evaluate(filterExpression, asset, field, value);
        });

        assertEquals("Invalid filter expression", exception.getMessage());
    }
}
