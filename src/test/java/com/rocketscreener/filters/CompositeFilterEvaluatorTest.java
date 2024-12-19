package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.FilterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CompositeFilterEvaluatorTest:
 * Unit tests for CompositeFilterEvaluator.
 */
@SpringBootTest
public class CompositeFilterEvaluatorTest {

    @Autowired
    private CompositeFilterEvaluator compositeFilterEvaluator;

    @MockBean
    private FilterRepository filterRepository;

    @Autowired
    private FilterService filterService;

    @BeforeEach
    void setUp() {
        // Define filters
        FilterRecord filterA = new FilterRecord(
                1,
                "FilterA",
                "metricA",
                new BigDecimal("50"),
                "greater_than",
                10,
                true,
                false,
                ""
        );

        FilterRecord filterB = new FilterRecord(
                2,
                "FilterB",
                "metricB",
                new BigDecimal("100"),
                "less_than",
                20,
                true,
                false,
                ""
        );

        FilterRecord compositeFilter = new FilterRecord(
                3,
                "CompositeFilter",
                "metricC",
                new BigDecimal("0"), // Not used for composite filters
                "none",
                30,
                true,
                true,
                "FilterA AND FilterB"
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filterA, filterB, compositeFilter));
    }

    @Test
    void testEvaluate_SimpleAnd() {
        String expression = "FilterA AND FilterB";
        String symbol = "SYM1";
        String metricA = "metricA";
        String metricB = "metricB";
        double valueA = 60.0; // > 50
        double valueB = 90.0; // < 100

        boolean result = compositeFilterEvaluator.evaluate(expression, symbol, metricA, valueA) &&
                         compositeFilterEvaluator.evaluate(expression, symbol, metricB, valueB);
        assertThat(result).isTrue();
    }

    @Test
    void testEvaluate_SimpleOr() {
        String expression = "FilterA OR FilterB";
        String symbol = "SYM2";
        String metricA = "metricA";
        String metricB = "metricB";
        double valueA = 40.0; // < 50
        double valueB = 110.0; // > 100

        boolean result = compositeFilterEvaluator.evaluate(expression, symbol, metricA, valueA) ||
                         compositeFilterEvaluator.evaluate(expression, symbol, metricB, valueB);
        assertThat(result).isTrue();
    }

    @Test
    void testEvaluate_CompositeExpression() {
        String expression = "FilterA AND ( FilterB OR CompositeFilter )";
        String symbol = "SYM3";
        String metricA = "metricA";
        String metricB = "metricB";
        String metricC = "metricC";
        double valueA = 60.0; // > 50
        double valueB = 90.0; // < 100
        double valueC = 0.0;  // Not used

        boolean result = compositeFilterEvaluator.evaluate(expression, symbol, metricA, valueA) &&
                         (compositeFilterEvaluator.evaluate(expression, symbol, metricB, valueB) ||
                          compositeFilterEvaluator.evaluate(expression, symbol, metricC, valueC));

        assertThat(result).isTrue();
    }

    @Test
    void testEvaluate_InvalidFilterName() {
        String expression = "FilterA AND FilterX";
        String symbol = "SYM4";
        String metricA = "metricA";
        String metricX = "metricX";
        double valueA = 60.0; // > 50
        double valueX = 30.0; // Unknown filter

        boolean result = compositeFilterEvaluator.evaluate(expression, symbol, metricA, valueA) &&
                         compositeFilterEvaluator.evaluate(expression, symbol, metricX, valueX);

        // FilterX does not exist, so evaluate should return false
        assertThat(result).isFalse();
    }

    // Add additional tests as needed
}
