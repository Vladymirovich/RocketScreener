package com.rocketscreener.templates;

import com.rocketscreener.storage.TemplateRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TemplateService:
 * - Fetches template body from DB (via TemplateRepository).
 * - Replaces placeholders {0}, {1}, ... with provided arguments.
 * - Provides the `generateResponse` method for quick string generation.
 * - If template not found, logs an error and returns a fallback string.
 */
@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);
    private final TemplateRepository templateRepo;

    public TemplateService(TemplateRepository templateRepo) {
        this.templateRepo = templateRepo;
    }

    /**
     * Generates a message response using a template and arguments.
     *
     * @param templateName the name of the template.
     * @param language     the language of the template.
     * @param args         the arguments to replace placeholders in the template.
     * @return the rendered message.
     */
    public String render(String templateName, String language, Object... args) {
        String body = templateRepo.getTemplateBody(templateName, language);
        if (body == null) {
            log.error("TemplateService: No template found for {} in {}", templateName, language);
            return "No template available.";
        }
        // Replace placeholders {0}, {1}, ...
        String msg = body;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            msg = msg.replace(placeholder, args[i] == null ? "" : args[i].toString());
        }
        return msg;
    }

    /**
     * Saves a template in the database.
     *
     * @param templateName the name of the template.
     * @param body         the content of the template.
     * @param eventType    the type of the event the template is for.
     * @param language     the language of the template.
     */
    public void saveTemplate(String templateName, String body, String eventType, String language) {
        templateRepo.saveTemplate(templateName, body, eventType, language);
        log.info("TemplateService: Saved template {} for language {}", templateName, language);
    }

    /**
     * A simplified version of `render` for use in bots and quick calls.
     * Defaults to "en" if language is not provided.
     *
     * @param templateName the name of the template.
     * @param args         the arguments to replace placeholders in the template.
     * @return the rendered message.
     */
    public String generateResponse(String templateName, Object... args) {
        return render(templateName, "en", args);
    }
}
