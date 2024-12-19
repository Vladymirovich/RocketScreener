package com.rocketscreener.storage;

/**
 * SourceRecord:
 * Represents a source with all necessary attributes.
 */
public record SourceRecord(
        int id,
        String name,
        String type,
        String baseUrl,
        String apiKey,
        int priority,
        boolean enabled
) {}
