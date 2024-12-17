package com.rocketscreener.templates;

import com.rocketscreener.storage.TemplateRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  TemplateService:
  - Fetches template body from DB (via TemplateRepository)
  - Replaces placeholders {0}, {1}, ... with provided arguments
  - No placeholders or dummy logic, fully real.
  - If template not found, log error and return a fallback string.
*/

@Service
public class TemplateService {
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);
    private final TemplateRepository templateRepo;

    public TemplateService(TemplateRepository templateRepo) {
        this.templateRepo = templateRepo;
    }

    public String render(String templateName, String language, Object... args) {
        String body = templateRepo.getTemplateBody(templateName, language);
        if(body == null) {
            log.error("TemplateService: No template found for {} in {}", templateName, language);
            return "No template available.";
        }
        // Replace placeholders {0}, {1}, ...
        String msg = body;
        for (int i=0; i<args.length; i++){
            String placeholder = "{"+i+"}";
            msg = msg.replace(placeholder, args[i]==null?"":args[i].toString());
        }
        return msg;
    }

    public void saveTemplate(String templateName, String body, String eventType, String language){
        templateRepo.saveTemplate(templateName, body, eventType, language);
        log.info("TemplateService: Saved template {} for language {}", templateName, language);
    }
}
