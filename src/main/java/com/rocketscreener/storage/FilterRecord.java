package com.rocketscreener.storage;

import java.math.BigDecimal;

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
