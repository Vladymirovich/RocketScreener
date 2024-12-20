package com.rocketscreener.templates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TemplateServiceTest {

    @InjectMocks
    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRenderTemplate() {
        String template = "Hello, {name}!";
        String lang = "en";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");

        String result = templateService.render(template, lang, data);
        assertEquals("Hello, John!", result);
    }
}
