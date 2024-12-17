package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FilterRepository {
    private final JdbcTemplate jdbc;

    public FilterRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<FilterRecord> findAllEnabled(){
        return jdbc.query("SELECT id,name,metric,threshold_value,threshold_type,time_interval_minutes,enabled,is_composite,composite_expression FROM filters WHERE enabled=TRUE",
                (rs, rn) -> new FilterRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("metric"),
                        rs.getBigDecimal("threshold_value"),
                        rs.getString("threshold_type"),
                        rs.getInt("time_interval_minutes"),
                        rs.getBoolean("enabled"),
                        rs.getBoolean("is_composite"),
                        rs.getString("composite_expression")
                ));
    }

    public void addFilter(String name, String metric, double threshold, String thresholdType, int interval, boolean composite, String expression){
        jdbc.update("INSERT INTO filters(name,metric,threshold_value,threshold_type,time_interval_minutes,is_composite,composite_expression) VALUES (?,?,?,?,?,?,?)",
                name, metric, threshold, thresholdType, interval, composite, expression);
    }

    // etc...
}
