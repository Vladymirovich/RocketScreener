package com.rocketscreener.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.math.BigDecimal;

@Repository
public class FilterRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public FilterRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addFilter(String name, String metric, BigDecimal thresholdValue, String thresholdType, int timeIntervalMinutes, boolean enabled, boolean isComposite, String compositeExpression) throws DataAccessException {
        String sql = "INSERT INTO filters (name, metric, threshold, threshold_type, interval, enabled, is_composite, composite_expression) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, name, metric, thresholdValue, thresholdType, timeIntervalMinutes, enabled, isComposite, compositeExpression);
    }

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

    public void setFilterEnabled(int id, boolean enabled) {
        String sql = "UPDATE filters SET enabled = ? WHERE id = ?";
        jdbc.update(sql, enabled, id);
    }

    // Другие CRUD операции по необходимости
}
