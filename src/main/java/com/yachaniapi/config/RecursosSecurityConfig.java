package com.yachaniapi.config;

import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class RecursosSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain recursosFilterChain(
            HttpSecurity http,
            UsuarioRepository usuarios,
            PasswordEncoder passwordEncoder) throws Exception {

        UserDetailsService detalles = correo -> {
            var usuario = usuarios.findByCorreoIgnoreCase(correo)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Credenciales incorrectas"
                            )
                    );

            return User.withUsername(usuario.getCorreo())
                    .password(usuario.getContrasenaHash())
                    .authorities("RECURSOS")
                    .build();
        };

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(detalles);

        provider.setPasswordEncoder(passwordEncoder);

        http
                .securityMatcher(
                        "/grupos/*/temas",
                        "/grupos/*/temas/**",
                        "/grupos/*/materiales",
                        "/grupos/*/materiales/**",
                        "/mazos",
                        "/mazos/**",
                        "/grupos/*/mazos",
                        "/grupos/*/mazos/**"
                )
                .authenticationProvider(provider)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(
                                (request, response, exception) -> {
                                    response.setStatus(401);
                                    response.setHeader(
                                            "WWW-Authenticate",
                                            "Basic realm=\"Yachani\""
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write(
                                            "{\"mensaje\":\"Correo o contraseña incorrectos o ausentes\"}"
                                    );
                                }
                        )
                        .accessDeniedHandler(
                                (request, response, exception) -> {
                                    response.setStatus(403);
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write(
                                            "{\"mensaje\":\"No tienes permiso para esta operación\"}"
                                    );
                                }
                        )
                );

        return http.build();
    }
}