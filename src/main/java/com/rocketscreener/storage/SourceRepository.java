package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SourceRepository {
    private final JdbcTemplate jdbc;

    public SourceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SourceRecord> findAllEnabledSources(){
        return jdbc.query("SELECT id, name, type, base_url, api_key, enabled, priority FROM sources WHERE enabled = TRUE",
                (rs, rowNum) -> new SourceRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("base_url"),
                        rs.getString("api_key"),
                        rs.getBoolean("enabled"),
                        rs.getInt("priority")
                ));
    }

    public void addSource(String name, String type, String baseUrl, String apiKey, int priority){
        jdbc.update("INSERT INTO sources(name,type,base_url,api_key,priority) VALUES (?,?,?,?,?)",
                name, type, baseUrl, apiKey, priority);
    }

    public void setSourceEnabled(int id, boolean enabled){
        jdbc.update("UPDATE sources SET enabled=? WHERE id=?", enabled, id);
    }

    // Other CRUD operations as needed
}
