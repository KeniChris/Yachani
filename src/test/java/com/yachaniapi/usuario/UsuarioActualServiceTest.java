package com.yachaniapi.usuario;

import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import com.yachaniapi.usuario.service.UsuarioActualService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioActualServiceTest {
    @Mock UsuarioRepository usuarios;

    @AfterEach
    void limpiar() { SecurityContextHolder.clearContext(); }

    private void autenticar(String id) {
        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256").subject(id).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
    }

    @Test
    void usaElSubjectNumericoEnLugarDelCorreo() {
        autenticar("4");
        var estudiante = new Estudiante();
        estudiante.setIdUsuario(4L);
        when(usuarios.findById(4L)).thenReturn(Optional.of(estudiante));
        assertEquals(4L, new UsuarioActualService(usuarios).idUsuario());
        verify(usuarios, never()).findByCorreoIgnoreCase(any());
    }

    @Test
    void impideActuarConElIdDeOtraPersona() {
        autenticar("4");
        var estudiante = new Estudiante();
        estudiante.setIdUsuario(4L);
        when(usuarios.findById(4L)).thenReturn(Optional.of(estudiante));
        assertThrows(AccessDeniedException.class,
                () -> new UsuarioActualService(usuarios).exigirPropio(9L));
    }

    @Test
    void sinAutenticacionRechazaLaOperacion() {
        assertThrows(BadCredentialsException.class,
                () -> new UsuarioActualService(usuarios).obtener());
    }

    @Test
    void rechazaUnSubjectSinIdValido() {
        autenticar("correo@upc.edu.pe");
        assertThrows(BadCredentialsException.class,
                () -> new UsuarioActualService(usuarios).obtener());
        verifyNoInteractions(usuarios);
    }

    @Test
    void unTutorNoPuedeRealizarOperacionesExclusivasDeEstudiante() {
        autenticar("4");
        var tutor = new Tutor();
        tutor.setIdUsuario(4L);
        when(usuarios.findById(4L)).thenReturn(Optional.of(tutor));
        assertThrows(AccessDeniedException.class,
                () -> new UsuarioActualService(usuarios).estudianteActual());
    }
}
