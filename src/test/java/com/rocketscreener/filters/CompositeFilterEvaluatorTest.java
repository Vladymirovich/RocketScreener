package com.rocketscreener.filters;

import com.rocketscreener.services.FilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

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

        when(filterService.evaluate(filterExpression, asset)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset);

        assertTrue(result, "The filter evaluation failed for OR condition.");
    }

    @Test
    void testEvaluateSimpleAnd() {
        String filterExpression = "price > 100 AND volume > 1000";
        String asset = "BTC";

        when(filterService.evaluate(filterExpression, asset)).thenReturn(true);

        boolean result = compositeFilterEvaluator.evaluate(filterExpression, asset);

        assertTrue(result, "The filter evaluation failed for AND condition.");
    }
}
