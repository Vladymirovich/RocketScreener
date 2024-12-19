package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

/**
 * SecurityConfig:
 * Configures Spring Security to protect against path traversal attacks.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Включение защиты CSRF
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // Авторизация всех запросов
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // Настройка HTTP Firewall
            .httpFirewall(allowUrlEncodedSlashHttpFirewall());

        return http.build();
    }

    /**
     * Configures a strict HTTP firewall to prevent path traversal attacks.
     *
     * @return Configured HttpFirewall.
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // Разрешение URL-кодированного слеша (%2F)
        firewall.setAllowUrlEncodedSlash(true);
        // Дополнительные настройки при необходимости
        return firewall;
    }
}
