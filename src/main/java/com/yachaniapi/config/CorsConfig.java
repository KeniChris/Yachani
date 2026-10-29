package com.yachaniapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Esto registra la configuración de CORS para la aplicación.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Esto obtiene los orígenes permitidos desde application.yaml.
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                // Esto permite las direcciones configuradas del frontend.
                .allowedOrigins(allowedOrigins)

                // Esto permite los métodos HTTP utilizados por la API.
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )

                // Esto permite cabeceras como Authorization y Content-Type.
                .allowedHeaders("*")

                // Esto permite guardar el resultado del preflight una hora.
                .maxAge(3600);
    }
}