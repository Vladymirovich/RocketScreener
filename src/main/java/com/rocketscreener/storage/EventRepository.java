package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;               
import java.sql.Timestamp;          
import java.time.LocalDateTime;     
import org.json.JSONObject;        

/**
 * EventRepository:
 * Handles CRUD operations for events in the database.
 */
@Repository
public class EventRepository {
    private final JdbcTemplate jdbc;
    private static final Logger logger = LoggerFactory.getLogger(EventRepository.class);

    @Autowired
    public EventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Добавляет новое событие в базу данных.
     *
     * @param eventType тип события
     * @param symbol    символ
     * @param source    источник
     * @param details   детали события в формате JSON
     */
    public void addEvent(String eventType, String symbol, String source, JSONObject details){
        try {
            jdbc.update("INSERT INTO events(event_type, symbol, source, details) VALUES (?, ?, ?, ?::jsonb)",
                    eventType, symbol, source, details.toString());
            logger.info("Event added successfully.");
        } catch (DataAccessException e) {
            logger.error("Error adding event: {}", e.getMessage());
            // Обработка исключения
        }
    }

    /**
     * Пример метода, использующего List и Timestamp.
     * Реализуйте логику получения событий из базы данных или другого источника.
     *
     * @return список событий
     */
    public List<EventRecord> fetchEvents() {
        // Пример реализации. Замените на реальную логику.
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
     * Пример использования метода fetchEvents.
     */
    public void someMethod() {
        List<EventRecord> events = fetchEvents();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        // Логика метода
        logger.info("Fetched {} events at {}", events.size(), currentTime);
    }

    /**
     * Находит последние события по типу и времени.
     *
     * @param eventType тип события
     * @param since     временная метка, с которой искать события
     * @return список последних событий
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

    /**
     * Сохраняет событие. Реализуйте логику сохранения события.
     *
     * @param event событие для сохранения
     */
    public void saveEvent(EventRecord event) {
        // Реализуйте логику сохранения события
    }
}
