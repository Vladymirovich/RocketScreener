package com.rocketscreener.filters;

import com.rocketscreener.services.FilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFilterEvaluatorTest {

    @Mock
    private FilterService filterService; // Мок для FilterService

    @InjectMocks
    private CompositeFilterEvaluator compositeFilterEvaluator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEvaluateSimpleOr() {
        String expression = "price > 100 OR volume > 1000";
        String asset = "BTC";
        String field = "price";
        double value = 150.0;

        when(filterService.evaluate(expression, asset, field, value)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(expression, asset, field, value);

        assertTrue(result, "Evaluation failed for OR condition.");
    }

    @Test
    void testEvaluateSimpleAnd() {
        String expression = "price > 100 AND volume > 2000";
        String asset = "ETH";
        String field = "volume";
        double value = 2500.0;

        when(filterService.evaluate(expression, asset, field, value)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(expression, asset, field, value);

        assertTrue(result, "Evaluation failed for AND condition.");
    }

    @Test
    void testEvaluateInvalidExpression() {
        String expression = "INVALID_EXPRESSION";
        String asset = "BTC";
        String field = "price";
        double value = 100.0;

        when(filterService.evaluate(expression, asset, field, value)).thenThrow(new IllegalArgumentException("Invalid expression"));

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> compositeFilterEvaluator.evaluate(expression, asset, field, value));

        assertEquals("Invalid expression", exception.getMessage());
    }
}
