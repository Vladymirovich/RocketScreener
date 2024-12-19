package com.rocketscreener.templates;

import com.rocketscreener.storage.TemplateRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TemplateService:
 * - Fetches template body from DB (via TemplateRepository)
 * - Replaces placeholders {0}, {1}, ... with provided arguments
 * - If template not found, logs error and returns a fallback string.
 */
@Service
public class TemplateService {
    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);
    private final TemplateRepository templateRepo;

    public TemplateService(TemplateRepository templateRepo) {
        this.templateRepo = templateRepo;
    }

    /**
     * Fetches and renders a template by replacing placeholders {0}, {1}, etc., with arguments.
     *
     * @param templateName the name of the template.
     * @param language     the language of the template.
     * @param args         the arguments to replace placeholders.
     * @return the rendered template.
     */
    public String render(String templateName, String language, Object... args) {
        String body = templateRepo.getTemplateBody(templateName, language);
        if (body == null) {
            log.error("TemplateService: No template found for {} in {}", templateName, language);
            return "No template available.";
        }

        String msg = body;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            msg = msg.replace(placeholder, args[i] == null ? "" : args[i].toString());
        }
        return msg;
    }

    /**
     * Saves a template into the repository.
     *
     * @param templateName the name of the template.
     * @param body         the body of the template.
     * @param eventType    the type of event for the template.
     * @param language     the language of the template.
     */
    public void saveTemplate(String templateName, String body, String eventType, String language) {
        templateRepo.saveTemplate(templateName, body, eventType, language);
        log.info("TemplateService: Saved template {} for language {}", templateName, language);
    }

    /**
     * Generates a response by rendering a template with arguments.
     *
     * @param templateName the name of the template to render.
     * @param args         the arguments to replace placeholders in the template.
     * @return the rendered response string.
     */
    public String generateResponse(String templateName, Object... args) {
        // Default to "en" (English) as the language if none is specified
        String language = "en";
        return render(templateName, language, args);
    }
}
