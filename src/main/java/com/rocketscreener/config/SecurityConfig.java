package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Включение защиты CSRF
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // Настройка правил авторизации
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll() // Разрешить доступ к публичным ресурсам
                .anyRequest().authenticated() // Требовать аутентификацию для всех остальных запросов
            )
            // Настройка HTTP Firewall для предотвращения Path Traversal атак
            .httpFirewall(strictHttpFirewall())
            // Настройка формы логина
            .formLogin();

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
        // Блокировка обратных слешей
        firewall.setAllowBackSlash(false);
        // Блокировка URL-кодированных процентов
        firewall.setAllowUrlEncodedPercent(false);
        // Блокировка других потенциально опасных символов
        firewall.setAllowUrlEncodedPeriod(false);
        firewall.setAllowUrlEncodedSemiColon(false);
        firewall.setAllowUrlEncodedComma(false);
        return firewall;
    }
}
