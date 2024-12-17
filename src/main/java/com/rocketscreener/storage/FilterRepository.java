package com.rocketscreener.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.List;

/**
 * FilterRepository:
 * Handles CRUD operations for filters in the database.
 */
@Repository
public class FilterRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public FilterRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Adds a new filter to the database.
     *
     * @param name                Name of the filter.
     * @param metric              Metric to monitor.
     * @param thresholdValue      Threshold value for the metric.
     * @param thresholdType       Type of threshold (e.g., "greater_than").
     * @param timeIntervalMinutes Time interval in minutes.
     * @param isComposite         Indicates if the filter is composite.
     * @param compositeExpression Composite expression for composite filters.
     * @throws DataAccessException If an error occurs during the database operation.
     */
    public void addFilter(String name, String metric, BigDecimal thresholdValue, String thresholdType, int timeIntervalMinutes, boolean isComposite, String compositeExpression) throws DataAccessException {
        String sql = "INSERT INTO filters (name, metric, threshold, threshold_type, interval, enabled, is_composite, composite_expression) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, name, metric, thresholdValue, thresholdType, timeIntervalMinutes, true, isComposite, compositeExpression);
    }

    /**
     * Retrieves all enabled filters from the database.
     *
     * @return List of enabled FilterRecord objects.
     */
    public List<FilterRecord> findAllEnabled() {
        String sql = "SELECT id, name, metric, threshold, threshold_type, interval, enabled, is_composite, composite_expression FROM filters WHERE enabled = TRUE";
        return jdbc.query(sql, (rs, rowNum) -> new FilterRecord(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("metric"),
                rs.getBigDecimal("threshold"),
                rs.getString("threshold_type"),
                rs.getInt("interval"),
                rs.getBoolean("enabled"),
                rs.getBoolean("is_composite"),
                rs.getString("composite_expression")
        ));
    }

    /**
     * Updates the enabled status of a filter.
     *
     * @param id      ID of the filter to update.
     * @param enabled New enabled status.
     */
    public void setFilterEnabled(int id, boolean enabled) {
        String sql = "UPDATE filters SET enabled = ? WHERE id = ?";
        jdbc.update(sql, enabled, id);
    }

    /**
     * Deletes a filter from the database.
     *
     * @param id ID of the filter to delete.
     * @throws DataAccessException If an error occurs during the database operation.
     */
    public void deleteFilter(int id) throws DataAccessException {
        String sql = "DELETE FROM filters WHERE id = ?";
        jdbc.update(sql, id);
    }

    /**
     * Retrieves a filter by its name.
     *
     * @param name Name of the filter.
     * @return FilterRecord object if found, else null.
     */
    public FilterRecord findByName(String name) {
        String sql = "SELECT id, name, metric, threshold, threshold_type, interval, enabled, is_composite, composite_expression FROM filters WHERE name = ? AND enabled = TRUE";
        List<FilterRecord> filters = jdbc.query(sql, new Object[]{name}, (rs, rowNum) -> new FilterRecord(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("metric"),
                rs.getBigDecimal("threshold"),
                rs.getString("threshold_type"),
                rs.getInt("interval"),
                rs.getBoolean("enabled"),
                rs.getBoolean("is_composite"),
                rs.getString("composite_expression")
        ));
        return filters.isEmpty() ? null : filters.get(0);
    }

    // Другие CRUD операции по необходимости
}
