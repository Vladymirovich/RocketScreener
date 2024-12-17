package com.rocketscreener.storage;

public record FilterRecord(
        int id,
        String name,
        String metric,
        double thresholdValue,
        String thresholdType,
        int interval,
        boolean enabled,
        Object additionalData
) {}
