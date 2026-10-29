package com.yachaniapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    /**
     * Permite acceder al registro y al inicio de sesión sin autenticación.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Esto integra la configuración CORS con Spring Security.
                .cors(Customizer.withDefaults())
                // Se desactiva CSRF porque estamos construyendo una API REST.
                .csrf(csrf -> csrf.disable())
                // Configuración de acceso a los endpoints.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}