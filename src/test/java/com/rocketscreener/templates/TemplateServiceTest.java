package com.rocketscreener.templates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Test
    void testGenerateResponse() {
        String input = "Hello";
        String expected = "You said: Hello";
        String actual = templateService.generateResponse(input);
        assertThat(actual).isEqualTo(expected);
    }

    // Add additional tests as needed
}
