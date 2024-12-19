package com.rocketscreener.filters;

import com.rocketscreener.storage.FilterRecord;
import com.rocketscreener.storage.FilterRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FilterServiceTest:
 * Unit tests for FilterService.
 */
@SpringBootTest
public class FilterServiceTest {

    @Autowired
    private FilterService filterService;

    @MockBean
    private FilterRepository filterRepository;

    @Test
    void testCheckSingleFilter_GreaterThan() {
        FilterRecord filter = new FilterRecord(
                1,
                "VolumeChange",
                "volume",
                new BigDecimal("10"),
                "greater_than",
                5,
                true,
                false,
                ""
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filter));

        boolean result = filterService.checkSingleFilter(filter, "BTC", "volume", 15.0);
        assertThat(result).isTrue();
    }

    @Test
    void testCheckSingleFilter_LessThan() {
        FilterRecord filter = new FilterRecord(
                2,
                "PriceDrop",
                "price",
                new BigDecimal("50000"),
                "less_than",
                10,
                true,
                false,
                ""
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filter));

        boolean result = filterService.checkSingleFilter(filter, "ETH", "price", 45000.0);
        assertThat(result).isTrue();
    }

    @Test
    void testCheckSingleFilter_Equal() {
        FilterRecord filter = new FilterRecord(
                3,
                "ExactMatch",
                "metric1",
                new BigDecimal("100"),
                "equal",
                15,
                true,
                false,
                ""
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filter));

        boolean resultTrue = filterService.checkSingleFilter(filter, "SYM1", "metric1", 100.0);
        boolean resultFalse = filterService.checkSingleFilter(filter, "SYM1", "metric1", 99.99);

        assertThat(resultTrue).isTrue();
        assertThat(resultFalse).isFalse();
    }

    @Test
    void testCheckSingleFilter_NotEqual() {
        FilterRecord filter = new FilterRecord(
                4,
                "NonExactMatch",
                "metric2",
                new BigDecimal("200"),
                "not_equal",
                20,
                true,
                false,
                ""
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filter));

        boolean resultTrue = filterService.checkSingleFilter(filter, "SYM2", "metric2", 199.99);
        boolean resultFalse = filterService.checkSingleFilter(filter, "SYM2", "metric2", 200.0);

        assertThat(resultTrue).isTrue();
        assertThat(resultFalse).isFalse();
    }

    @Test
    void testCheckSingleFilter_MetricMismatch() {
        FilterRecord filter = new FilterRecord(
                5,
                "MetricMismatch",
                "metric3",
                new BigDecimal("300"),
                "greater_than",
                25,
                true,
                false,
                ""
        );

        Mockito.when(filterRepository.findAllEnabled()).thenReturn(List.of(filter));

        boolean result = filterService.checkSingleFilter(filter, "SYM3", "metric4", 350.0);
        assertThat(result).isFalse();
    }

    // Add additional tests as needed
}
