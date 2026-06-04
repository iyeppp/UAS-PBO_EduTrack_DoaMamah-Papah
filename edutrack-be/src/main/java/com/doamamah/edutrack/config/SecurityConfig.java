package com.doamamah.edutrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfigurasi Spring Security untuk EduTrack.
 *
 * - CSRF dinonaktifkan agar frontend JavaFX bisa mengirim POST/PUT/DELETE tanpa token CSRF.
 * - Semua endpoint di-permit agar REST API bisa diakses bebas.
 * - frameOptions diset sameOrigin agar H2 Console (yang menggunakan iframe) bisa diakses via browser.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}
