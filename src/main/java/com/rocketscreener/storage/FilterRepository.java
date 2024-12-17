package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Repository
public class FilterRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public FilterRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addFilter(String name, String metric, double threshold, String thresholdType, int interval, boolean enabled, Object additionalData) throws DataAccessException {
        String sql = "INSERT INTO filters (name, metric, threshold, threshold_type, interval, enabled, additional_data) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql, name, metric, threshold, thresholdType, interval, enabled, additionalData);
    }

    public List<FilterRecord> findAllEnabled() {
        String sql = "SELECT * FROM filters WHERE enabled = TRUE";
        return jdbc.query(sql, (rs, rowNum) -> new FilterRecord(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("metric"),
                rs.getDouble("threshold"),
                rs.getString("threshold_type"),
                rs.getInt("interval"),
                rs.getBoolean("enabled"),
                rs.getObject("additional_data")
        ));
    }

    // Другие методы репозитория
}
