package com.rocketscreener.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationsRepository {
    private final JdbcTemplate jdbc;

    public NotificationsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void logNotification(int eventId, String templateName, String message, String chatId) {
        jdbc.update("INSERT INTO notifications(event_id,template_name,message,chat_id) VALUES (?,?,?,?)",
                eventId, templateName, message, chatId);
    }
}
