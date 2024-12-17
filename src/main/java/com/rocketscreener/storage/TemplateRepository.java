package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TemplateRepository {
    private final JdbcTemplate jdbc;

    public TemplateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String getTemplateBody(String templateName, String language) {
        return jdbc.queryForObject("SELECT template_body FROM templates WHERE template_name=? AND language=?",
                new Object[]{templateName, language}, String.class);
    }

    public void saveTemplate(String templateName, String body, String eventType, String language) {
        jdbc.update("INSERT INTO templates(template_name,template_body,event_type,language) VALUES (?,?,?,?) ON CONFLICT(template_name,language) DO UPDATE SET template_body=EXCLUDED.template_body,event_type=EXCLUDED.event_type",
                templateName, body, eventType, language);
    }
}
