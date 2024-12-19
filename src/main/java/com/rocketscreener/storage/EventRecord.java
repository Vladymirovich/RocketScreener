package com.rocketscreener.storage;

import org.json.JSONObject;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public record EventRecord(
        int id,
        String data,
        String eventType,
        String symbol,
        String source,
        Timestamp timestamp,
        LocalDateTime createdAt,
        JSONObject details
){}
