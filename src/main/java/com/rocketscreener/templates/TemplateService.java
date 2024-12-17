package com.rocketscreener.templates;

import com.rocketscreener.storage.TemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {
    private final TemplateRepository templateRepo;

    public TemplateService(TemplateRepository templateRepo) {
        this.templateRepo = templateRepo;
    }

    public String render(String templateName, String language, Object... args) {
        String body = templateRepo.getTemplateBody(templateName, language);
        if(body == null) {
            body = "No template found for " + templateName + " in " + language;
        }
        // Simple string format
        return body.formatted(args);
    }

    public void saveTemplate(String templateName, String body, String eventType, String language){
        templateRepo.saveTemplate(templateName, body, eventType, language);
    }
}
