package com.rocketscreener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
            // Disable CSRF for simplicity; adjust as needed
            .csrf(csrf -> csrf.disable())
            // Authorize all requests
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // Configure HTTP Firewall
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
        // Allow URL encoded slash (%2F)
        firewall.setAllowUrlEncodedSlash(true);
        // Add more configurations if necessary
        return firewall;
    }
}
