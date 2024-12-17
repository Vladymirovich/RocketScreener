package com.rocketscreener.storage;

public record SourceRecord(
    int id,
    String name,
    String type,
    String baseUrl,
    String apiKey,
    boolean enabled,
    int priority
){}
