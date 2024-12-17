package com.rocketscreener.services;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.sql.Timestamp;

@Service
public class ArchiveService {
    private final JdbcTemplate jdbc;
    private final int archiveThresholdYears;
    private final boolean monthlyBackups;

    public ArchiveService(JdbcTemplate jdbc, Dotenv dotenv) {
        this.jdbc = jdbc;
        this.archiveThresholdYears = Integer.parseInt(dotenv.get("ARCHIVE_THRESHOLD_YEARS","1"));
        this.monthlyBackups = Boolean.parseBoolean(dotenv.get("MONTHLY_BACKUPS_ENABLED","true"));
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Every 1st day of month
    public void archiveOldData() {
        Timestamp cutoff = Timestamp.from(Instant.now().minus(archiveThresholdYears, ChronoUnit.YEARS));
        // Move old data to another table or export to a file
        // For demo: Just delete old data
        jdbc.update("DELETE FROM historical_data WHERE timestamp < ?", cutoff);
        // Backups can be created using pg_dump externally, provide link via admin-bot
    }
}
