package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Создание экземпляра StrictHttpFirewall
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSemicolon(true); // Исправлено написание
        firewall.setAllowUrlEncodedComma(true);     // Убедитесь, что метод существует

        // Настройка HttpSecurity
        http
            .csrf()
            .and()
            .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
            .httpFirewall(firewall); // Убедитесь, что метод httpFirewall доступен

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
