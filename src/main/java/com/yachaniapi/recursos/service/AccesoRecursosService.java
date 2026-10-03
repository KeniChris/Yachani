package com.yachaniapi.recursos.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.repository.GrupoRecursosRepository;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccesoRecursosService {

    private final GrupoRecursosRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;

    public AccesoRecursosService(
            GrupoRecursosRepository grupoRepository,
            UsuarioRepository usuarioRepository) {

        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Se utiliza dentro de las transacciones de los otros servicios.
    public AccesoGrupo obtener(Long idGrupo, boolean bloquear) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {

            throw new RecursosException(
                    HttpStatus.UNAUTHORIZED,
                    "Inicia sesión para acceder a los recursos"
            );
        }

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(auth.getName())
                .orElseThrow(() ->
                        new RecursosException(
                                HttpStatus.UNAUTHORIZED,
                                "El usuario autenticado no existe"
                        )
                );

        GrupoEstudio grupo = (
                bloquear
                        ? grupoRepository.buscarConBloqueo(idGrupo)
                        : grupoRepository.findById(idGrupo)
        ).orElseThrow(() ->
                new RecursosException(
                        HttpStatus.NOT_FOUND,
                        "El grupo de estudio no existe"
                )
        );

        boolean administrador = grupo.getCreador()
                .getIdUsuario()
                .equals(usuario.getIdUsuario());

        boolean participante = grupo.getParticipantes()
                .stream()
                .anyMatch(participanteGrupo ->
                        participanteGrupo.getIdUsuario()
                                .equals(usuario.getIdUsuario())
                );

        if (!administrador && !participante) {
            throw new RecursosException(
                    HttpStatus.FORBIDDEN,
                    "No perteneces a este grupo"
            );
        }

        return new AccesoGrupo(
                grupo,
                usuario,
                administrador
        );
    }

    public record AccesoGrupo(
            GrupoEstudio grupo,
            Usuario usuario,
            boolean administrador
    ) {}
}