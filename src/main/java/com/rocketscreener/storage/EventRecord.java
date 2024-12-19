package com.rocketscreener.storage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.json.JSONObject;

/**
 * EventRecord:
 * Represents an event with all necessary details.
 */
public record EventRecord(
    int id,
    String eventType,
    String name,
    String description,
    String location,
    String organizer,
    Timestamp eventTime,
    LocalDateTime createdAt,
    JSONObject metadata
) {
    // Дополнительные методы, если необходимо
}
