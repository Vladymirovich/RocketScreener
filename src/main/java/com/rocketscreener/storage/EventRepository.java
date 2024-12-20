package com.rocketscreener.storage;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * EventRepository:
 * Handles CRUD operations for events in the database.
 */
@Repository
public class EventRepository {

    private static final Logger logger = LoggerFactory.getLogger(EventRepository.class);
    private final JdbcTemplate jdbc;

    @Autowired
    public EventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Adds a new event to the database.
     *
     * @param eventType Type of the event.
     * @param symbol    Symbol associated with the event.
     * @param source    Source of the event.
     * @param details   Details of the event in JSON format.
     */
    public void addEvent(String eventType, String symbol, String source, JSONObject details) {
        try {
            jdbc.update("INSERT INTO events(event_type, symbol, source, details) VALUES (?, ?, ?, ?::jsonb)",
                    eventType, symbol, source, details.toString());
            logger.info("Event added successfully.");
        } catch (DataAccessException e) {
            logger.error("Error adding event: {}", e.getMessage());
            // Handle exception
        }
    }

    /**
     * Fetches all events from the database.
     *
     * @return List of EventRecord objects.
     */
    public List<EventRecord> fetchEvents() {
        String sql = "SELECT id, event_type, name, description, location, organizer, event_time, created_at, details FROM events";
        return jdbc.query(sql, (rs, rowNum) -> new EventRecord(
                rs.getInt("id"),
                rs.getString("event_type"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getString("organizer"),
                rs.getTimestamp("event_time"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                new JSONObject(rs.getString("details"))
        ));
    }

    /**
     * Finds recent events by type and time.
     *
     * @param eventType Type of the event.
     * @param since     Timestamp to filter events.
     * @return List of recent EventRecord objects.
     */
    public List<EventRecord> findRecentEvents(String eventType, Timestamp since) {
        String sql = "SELECT id, event_type, name, description, location, organizer, event_time, created_at, details " +
                     "FROM events WHERE event_type = ? AND event_time > ? ORDER BY event_time DESC";
        return jdbc.query(sql, (rs, rn) -> new EventRecord(
                rs.getInt("id"),
                rs.getString("event_type"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getString("organizer"),
                rs.getTimestamp("event_time"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                new JSONObject(rs.getString("details"))
        ), eventType, since);
    }
}
