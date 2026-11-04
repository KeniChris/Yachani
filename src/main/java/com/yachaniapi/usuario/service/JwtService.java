package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final Duration duracion;

    public JwtService(JwtEncoder encoder,
            @Value("${app.security.jwt-expiration-minutes:60}") long minutos) {
        if (minutos <= 0) throw new IllegalArgumentException("La duración del token debe ser positiva");
        this.encoder = encoder;
        this.duracion = Duration.ofMinutes(minutos);
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getIdUsuario().toString())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(duracion))
                .claim("role", usuario instanceof Estudiante ? "ESTUDIANTE" : "TUTOR")
                .build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    public long segundosDeValidez() {
        return duracion.toSeconds();
    }
}
