package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.exception.ConflictoGrupoException;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoNoEncontradoException;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.usuario.service.UsuarioActualService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccesoGrupoService {
    private final GrupoEstudioRepository grupos;
    private final UsuarioActualService usuarioActual;
    private final ReglasGrupoService reglas;

    public GrupoEstudio miembro(Long idGrupo, boolean bloquear) {
        Long idUsuario = usuarioActual.idUsuario();
        GrupoEstudio grupo = (bloquear ? grupos.buscarConBloqueo(idGrupo) : grupos.findById(idGrupo))
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
        if (!reglas.esIntegrante(grupo, idUsuario)) {
            throw new AccessDeniedException("No perteneces a este grupo");
        }
        if (bloquear && !"ACTIVO".equals(grupo.getEstado())) {
            throw new ConflictoGrupoException("El grupo no está activo");
        }
        return grupo;
    }

    public GrupoEstudio administrador(Long idGrupo, boolean bloquear) {
        GrupoEstudio grupo = miembro(idGrupo, bloquear);
        if (!reglas.esAdministrador(grupo, usuarioActual.idUsuario())) {
            throw new AccessDeniedException("Solo quien administra este grupo puede realizar la operación");
        }
        return grupo;
    }
}
