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
        firewall.setAllowSemicolon(true); // Совместимо с актуальной версией Spring Security
        firewall.setAllowUrlEncodedPercent(true);

        // Настройка HttpSecurity с использованием актуальных методов
        http
            .csrf(csrf -> csrf.disable()) // Отключение CSRF с учетом совместимости
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll() // Открытые эндпоинты
                .anyRequest().authenticated() // Остальные эндпоинты требуют аутентификации
            );

        return http.build();
    }

    /**
     * Настройка строгого HTTP Firewall.
     *
     * @return экземпляр HttpFirewall
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
