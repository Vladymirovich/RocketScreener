package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

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
            .httpFirewall(strictHttpFirewall());

        return http.build();
    }

    /**
     * Настройка строгого HTTP Firewall для предотвращения атак Path Traversal.
     *
     * @return настроенный HttpFirewall.
     */
    @Bean
    public HttpFirewall strictHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // Блокировка URL-кодированных слешей
        firewall.setAllowUrlEncodedSlash(false);
        // Дополнительные настройки при необходимости
        return firewall;
    }
}
