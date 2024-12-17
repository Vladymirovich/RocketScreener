package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbc;

    public EventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addEvent(String eventType, String symbol, String source, JSONObject details){
        jdbc.update("INSERT INTO events(event_type,symbol,source,details) VALUES (?,?,?,?::jsonb)",
                eventType, symbol, source, details.toString());
    }

    public List<EventRecord> findRecentEvents(String eventType, Timestamp since) {
        return jdbc.query("SELECT id,event_type,symbol,source,timestamp,details FROM events WHERE event_type=? AND timestamp>? ORDER BY timestamp DESC",
                (rs, rn) -> new EventRecord(
                        rs.getInt("id"),
                        rs.getString("event_type"),
                        rs.getString("symbol"),
                        rs.getString("source"),
                        rs.getTimestamp("timestamp"),
                        new JSONObject(rs.getString("details"))
                ), eventType, since);
    }
}
