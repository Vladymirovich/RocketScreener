package com.rocketscreener.storage;

import java.math.BigDecimal;

/**
 * FilterRecord:
 * Represents a filter with all necessary attributes, including support for composite filters.
 */
public record FilterRecord(
        int id,
        String name,
        String metric,
        BigDecimal thresholdValue,
        String thresholdType,
        int timeIntervalMinutes,
        boolean enabled,
        boolean isComposite,
        String compositeExpression
) {}
