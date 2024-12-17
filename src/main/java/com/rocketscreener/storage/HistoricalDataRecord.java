package com.rocketscreener.storage;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record HistoricalDataRecord(
        int id,
        String symbol,
        String metric,
        Timestamp timestamp,
        BigDecimal value
){}
