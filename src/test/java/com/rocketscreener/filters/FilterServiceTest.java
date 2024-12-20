package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FilterServiceTest {

    @Mock
    private FilterRepository filterRepo;

    @InjectMocks
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEvaluateValidExpression() {
        String filterExpression = "price > 100";
        String asset = "BTC";
        String field = "price";
        double value = 150.0;

        // Mock the behavior of validateExpression in FilterService
        when(filterService.validateExpression(filterExpression)).thenReturn(true);
        boolean result = filterService.evaluate(filterExpression, asset, field, value);

        assertTrue(result, "Expected the filter expression to evaluate as true.");
    }

    @Test
    void testEvaluateInvalidExpression() {
        String filterExpression = "INVALID_EXPRESSION";
        String asset = "BTC";
        String field = "price";
        double value = 100.0;

        // Mock the behavior of validateExpression in FilterService
        when(filterService.validateExpression(filterExpression)).thenReturn(false);

        boolean result = filterService.evaluate(filterExpression, asset, field, value);

        assertFalse(result, "Expected the filter expression to evaluate as false.");
    }

    @Test
    void testSaveFilter() {
        String filterExpression = "price > 100";
        filterService.saveFilter(filterExpression);

        verify(filterRepo, times(1)).addFilter(
            eq(filterExpression), 
            eq("metric"), 
            any(BigDecimal.class), 
            eq("greater_than"), 
            eq(10), 
            eq(false), 
            isNull()
        );
    }

    @Test
    void testDeleteFilter() {
        int filterId = 123;

        filterService.deleteFilter(filterId);

        verify(filterRepo, times(1)).deleteFilter(filterId);
    }
}
