package com.yachaniapi.config;

import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class JwtSecurityTest {
    private final JwtConfig config = new JwtConfig();
    private final javax.crypto.SecretKey key = new SecretKeySpec(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    @Test
    void emiteIdRolYVencimientoYValidaLaFirma() {
        var usuario = new Estudiante();
        usuario.setIdUsuario(7L);
        JwtService service = new JwtService(config.jwtEncoder(key), 60);
        Jwt token = config.jwtDecoder(key).decode(service.generar(usuario));
        assertEquals("7", token.getSubject());
        assertEquals("ESTUDIANTE", token.getClaimAsString("role"));
        assertTrue(token.getExpiresAt().isAfter(Instant.now()));
        assertEquals(3600, service.segundosDeValidez());
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() {
        var otra = new SecretKeySpec(
                "abcdefghijklmnopqrstuvwxyz012345".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        var usuario = new Estudiante();
        usuario.setIdUsuario(7L);
        String token = new JwtService(config.jwtEncoder(otra), 60).generar(usuario);
        assertThrows(JwtException.class, () -> config.jwtDecoder(key).decode(token));
    }

    @Test
    void rechazaUnTokenVencidoAunqueLaFirmaSeaCorrecta() {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("7")
                .issuedAt(ahora.minusSeconds(7200)).expiresAt(ahora.minusSeconds(300))
                .claim("role", "ESTUDIANTE").build();
        String token = config.jwtEncoder(key).encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        assertThrows(JwtException.class, () -> config.jwtDecoder(key).decode(token));
    }

    @Test
    void noAdmiteUnaClaveSecretaCorta() {
        assertThrows(IllegalStateException.class, () -> config.jwtKey("corta"));
    }
}
