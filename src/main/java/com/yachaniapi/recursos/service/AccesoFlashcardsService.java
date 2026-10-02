package com.yachaniapi.recursos.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.recursos.entity.*;
import com.yachaniapi.recursos.exception.FlashcardsException;
import com.yachaniapi.recursos.repository.*;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccesoFlashcardsService {

    private final UsuarioRepository usuarios;
    private final MazoFlashcardsRepository mazos;
    private final MazoCompartidoRepository compartidos;

    public Usuario usuarioActual() {
        var autenticacion = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || autenticacion instanceof AnonymousAuthenticationToken) {

            throw new FlashcardsException(
                    HttpStatus.UNAUTHORIZED,
                    "Inicia sesión para acceder a las flashcards"
            );
        }

        return usuarios.findByCorreoIgnoreCase(
                autenticacion.getName()
        ).orElseThrow(() -> new FlashcardsException(
                HttpStatus.UNAUTHORIZED,
                "El usuario autenticado no existe"
        ));
    }

    public MazoFlashcards buscar(
            Long idMazo,
            boolean bloquear
    ) {
        var resultado = bloquear
                ? mazos.buscarConBloqueo(idMazo)
                : mazos.findById(idMazo);

        return resultado.orElseThrow(() ->
                new FlashcardsException(
                        HttpStatus.NOT_FOUND,
                        "El mazo no existe"
                )
        );
    }

    public MazoFlashcards propio(
            Long idMazo,
            boolean bloquear
    ) {
        var usuario = usuarioActual();
        var mazo = buscar(idMazo, bloquear);

        if (!esAutor(mazo, usuario)) {
            throw new FlashcardsException(
                    HttpStatus.FORBIDDEN,
                    "Solo el autor puede modificar o consultar su mazo personal"
            );
        }

        return mazo;
    }

    public Contexto paraPracticar(
            Long idMazo,
            boolean bloquear
    ) {
        var usuario = usuarioActual();
        var mazo = buscar(idMazo, bloquear);

        boolean compartido =
                mazo.getEstado() == EstadoMazo.PUBLICADO
                        && compartidos.findByMazo_IdMazo(idMazo)
                        .stream()
                        .anyMatch(registro ->
                                esMiembro(registro.getGrupo(), usuario)
                        );

        if (!esAutor(mazo, usuario) && !compartido) {
            throw new FlashcardsException(
                    HttpStatus.FORBIDDEN,
                    "No tienes acceso para practicar este mazo"
            );
        }

        return new Contexto(mazo, usuario);
    }

    public boolean esAutor(
            MazoFlashcards mazo,
            Usuario usuario
    ) {
        return mazo.getCreador().getIdUsuario()
                .equals(usuario.getIdUsuario());
    }

    private boolean esMiembro(
            GrupoEstudio grupo,
            Usuario usuario
    ) {
        return grupo.getCreador().getIdUsuario()
                .equals(usuario.getIdUsuario())
                || grupo.getParticipantes().stream()
                .anyMatch(participante ->
                        participante.getIdUsuario()
                                .equals(usuario.getIdUsuario())
                );
    }

    public record Contexto(
            MazoFlashcards mazo,
            Usuario usuario
    ) {
    }
}