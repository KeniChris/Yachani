package com.yachaniapi.recursos.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.service.ReglasGrupoService;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.repository.GrupoRecursosRepository;
import com.yachaniapi.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccesoRecursosService {

    private final GrupoRecursosRepository grupoRepository;
    private final com.yachaniapi.usuario.service.UsuarioActualService usuarioActual;
    private final ReglasGrupoService reglas;

    // Se invoca dentro de las transacciones de los servicios de recursos.
    public AccesoGrupo obtener(Long idGrupo, boolean bloquear) {
        Usuario usuario = usuarioActual.obtener();
        GrupoEstudio grupo = (bloquear
                ? grupoRepository.buscarConBloqueo(idGrupo)
                : grupoRepository.findById(idGrupo))
                .orElseThrow(() -> new RecursosException(HttpStatus.NOT_FOUND,
                        "El grupo de estudio no existe"));
        if (!reglas.esIntegrante(grupo, usuario.getIdUsuario())) {
            throw new RecursosException(HttpStatus.FORBIDDEN, "No perteneces a este grupo");
        }
        return new AccesoGrupo(grupo, usuario,
                reglas.esAdministrador(grupo, usuario.getIdUsuario()));
    }

    public AccesoGrupo obtenerParaPublicar(Long idGrupo) {
        AccesoGrupo contexto = obtener(idGrupo, true);
        if (!reglas.puedePublicarRecursos(contexto.grupo(), contexto.usuario())) {
            throw new RecursosException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para publicar materiales o flashcards en este grupo");
        }
        return contexto;
    }

    public AccesoGrupo obtenerParaGestionarTemas(Long idGrupo) {
        AccesoGrupo contexto = obtener(idGrupo, true);
        if (!reglas.puedeGestionarTemas(contexto.grupo(), contexto.usuario())) {
            throw new RecursosException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para gestionar los temas de este grupo");
        }
        return contexto;
    }

    public record AccesoGrupo(
            GrupoEstudio grupo,
            Usuario usuario,
            boolean administrador
    ) {}
}
