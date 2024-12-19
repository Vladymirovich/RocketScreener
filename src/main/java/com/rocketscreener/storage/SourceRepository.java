package com.rocketscreener.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * SourceRepository:
 * Handles CRUD operations for sources in the database.
 */
@Repository
public class SourceRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public SourceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Adds a new source to the database.
     *
     * @param name     Name of the source.
     * @param type     Type of the source.
     * @param baseUrl  Base URL of the source API.
     * @param apiKey   API key for the source.
     * @param priority Priority of the source.
     * @throws DataAccessException If an error occurs during the database operation.
     */
    public void addSource(String name, String type, String baseUrl, String apiKey, int priority) throws DataAccessException {
        String sql = "INSERT INTO sources (name, type, base_url, api_key, priority, enabled) VALUES (?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, name, type, baseUrl, apiKey, priority, true);
    }

    /**
     * Retrieves all enabled sources from the database.
     *
     * @return List of enabled SourceRecord objects.
     */
    public List<SourceRecord> findAllEnabledSources() {
        String sql = "SELECT id, name, type, base_url, api_key, priority, enabled FROM sources WHERE enabled = TRUE";
        return jdbc.query(sql, (rs, rowNum) -> new SourceRecord(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("base_url"),
                rs.getString("api_key"),
                rs.getInt("priority"),
                rs.getBoolean("enabled")
        ));
    }

    // Other CRUD operations as needed
}
