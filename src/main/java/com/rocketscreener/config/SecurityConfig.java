package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * SecurityConfig:
 * Конфигурация безопасности для проекта RocketScreener.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Настройка цепочки фильтров безопасности.
     *
     * @param http объект конфигурации безопасности.
     * @return настроенная цепочка фильтров безопасности.
     * @throws Exception если возникают ошибки конфигурации.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Настройка CSRF защиты с использованием CookieCsrfTokenRepository
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll() // Открытые эндпоинты
                .anyRequest().authenticated() // Остальные эндпоинты требуют аутентификации
            );

        return http.build();
    }

    /**
     * Настройка строгого HTTP Firewall.
     *
     * @return экземпляр HttpFirewall.
     */
    @Bean
    public HttpFirewall strictHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(false);
        firewall.setAllowUrlEncodedSlash(false);
        firewall.setAllowBackSlash(false);
        firewall.setAllowUrlEncodedPercent(false);
        firewall.setAllowUrlEncodedPeriod(false);
        return firewall;
    }
}
