package com.rocketscreener.storage;

import org.json.JSONObject;

import java.sql.Timestamp;

public record EventRecord(
        int id,
        String eventType,
        String symbol,
        String source,
        Timestamp timestamp,
        JSONObject details
){}
