package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

/**
 * HistoricalDataRepository:
 * Handles storage and retrieval of historical data.
 */
@Repository
public class HistoricalDataRepository {
    private final JdbcTemplate jdbc;

    public HistoricalDataRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Saves historical data for a given symbol and metric.
     *
     * @param symbol Symbol of the asset.
     * @param metric Metric to track.
     * @param ts     Timestamp of the data point.
     * @param value  Value of the metric.
     */
    public void saveData(String symbol, String metric, Timestamp ts, double value) {
        jdbc.update("INSERT INTO historical_data(symbol, metric, timestamp, value) VALUES (?,?,?,?)", symbol, metric, ts, value);
    }

    /**
     * Retrieves historical data for a given interval.
     *
     * @param symbol Symbol of the asset.
     * @param metric Metric to track.
     * @param from   Start timestamp.
     * @param to     End timestamp.
     * @return List of HistoricalDataRecord objects.
     */
    public List<HistoricalDataRecord> getDataForInterval(String symbol, String metric, Timestamp from, Timestamp to) {
        return jdbc.query("SELECT id, symbol, metric, timestamp, value FROM historical_data WHERE symbol=? AND metric=? AND timestamp BETWEEN ? AND ? ORDER BY timestamp",
                (rs, rn) -> new HistoricalDataRecord(
                        rs.getInt("id"),
                        rs.getString("symbol"),
                        rs.getString("metric"),
                        rs.getTimestamp("timestamp"),
                        rs.getBigDecimal("value")
                ), symbol, metric, from, to);
    }
}
