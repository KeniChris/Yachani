package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioActualService {
    private final UsuarioRepository usuarios;

    public Usuario obtener() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new BadCredentialsException("Inicia sesión para continuar");
        }
        Long id;
        try {
            id = Long.valueOf(jwt.getSubject());
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            throw new BadCredentialsException("El token no identifica a un usuario válido");
        }
        return usuarios.findById(id).orElseThrow(() ->
                new BadCredentialsException("El usuario autenticado no existe"));
    }

    public Long idUsuario() {
        return obtener().getIdUsuario();
    }

    public Usuario exigirPropio(Long idEnviado) {
        Usuario usuario = obtener();
        if (idEnviado != null && !usuario.getIdUsuario().equals(idEnviado)) {
            throw new AccessDeniedException("No puedes actuar en nombre de otro usuario");
        }
        return usuario;
    }

    public Estudiante estudianteActual() {
        if (obtener() instanceof Estudiante estudiante) return estudiante;
        throw new AccessDeniedException("Esta operación corresponde a un estudiante");
    }

    public Tutor tutorActual() {
        if (obtener() instanceof Tutor tutor) return tutor;
        throw new AccessDeniedException("Esta operación corresponde a un tutor");
    }
}
