package com.rocketscreener.events;

import com.rocketscreener.storage.EventRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepo;

    public EventService(EventRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    public void recordEvent(String eventType, String symbol, String source, JSONObject details) {
        eventRepo.addEvent(eventType, symbol, source, details);
    }
}
