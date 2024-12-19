package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Репозиторий для работы с шаблонами.
 */
@Repository
public class TemplateRepository {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);
    private final JdbcTemplate jdbc;

    public TemplateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Получение шаблона по имени и языку.
     */
    public String getTemplateBody(String templateName, String language) {
        try {
            return jdbc.queryForObject(
                "SELECT template_body FROM templates WHERE template_name=? AND language=?",
                new Object[]{templateName, language},
                String.class
            );
        } catch (Exception e) {
            logger.error("Error fetching template: {}, Language: {}", templateName, language, e);
            return null;
        }
    }

    /**
     * Сохранение нового или обновление существующего шаблона.
     */
    public void saveTemplate(String templateName, String body, String eventType, String language) {
        try {
            jdbc.update(
                "INSERT INTO templates(template_name, template_body, event_type, language) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(template_name, language) DO UPDATE SET " +
                "template_body=EXCLUDED.template_body, event_type=EXCLUDED.event_type",
                templateName, body, eventType, language
            );
            logger.info("Template {} for language {} saved successfully.", templateName, language);
        } catch (Exception e) {
            logger.error("Error saving template: {}, Language: {}", templateName, language, e);
        }
    }
}
