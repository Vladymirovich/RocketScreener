package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  TemplateRepository:
  - Fetches template_body from templates table
  - Saves/updates templates
  - No placeholders, real DB operations
*/

@Repository
public class TemplateRepository {
    private static final Logger log = LoggerFactory.getLogger(TemplateRepository.class);
    private final JdbcTemplate jdbc;

    public TemplateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String getTemplateBody(String templateName, String language) {
        try {
            return jdbc.queryForObject("SELECT template_body FROM templates WHERE template_name=? AND language=?",
                    new Object[]{templateName, language}, String.class);
        } catch(Exception e){
            log.error("TemplateRepository: Error fetching template {} for language {}", templateName, language, e);
            return null;
        }
    }

    public void saveTemplate(String templateName, String body, String eventType, String language) {
        try {
            jdbc.update("INSERT INTO templates(template_name,template_body,event_type,language) VALUES (?,?,?,?) " +
                            "ON CONFLICT(template_name,language) DO UPDATE SET template_body=EXCLUDED.template_body,event_type=EXCLUDED.event_type",
                    templateName, body, eventType, language);
        } catch(Exception e){
            log.error("TemplateRepository: Error saving template {} for language {}", templateName, language, e);
        }
    }
}
