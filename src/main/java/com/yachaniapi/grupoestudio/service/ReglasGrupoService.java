package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.usuario.entity.Usuario;
import org.springframework.stereotype.Service;

@Service
public class ReglasGrupoService {

    public boolean esAdministrador(GrupoEstudio grupo, Long idUsuario) {
        return idUsuario != null
                && idUsuario.equals(grupo.getCreador().getIdUsuario());
    }

    public boolean esIntegrante(GrupoEstudio grupo, Long idUsuario) {
        return esAdministrador(grupo, idUsuario)
                || (idUsuario != null && grupo.getParticipantes().stream()
                .anyMatch(p -> idUsuario.equals(p.getIdUsuario())));
    }

    public int integrantesActuales(GrupoEstudio grupo) {
        int cantidad = (int) grupo.getParticipantes().stream()
                .map(Usuario::getIdUsuario).distinct().count();
        boolean faltaCreador = grupo.getTipoGrupo() == TipoGrupo.ESTUDIANTES
                && grupo.getParticipantes().stream().noneMatch(p ->
                grupo.getCreador().getIdUsuario().equals(p.getIdUsuario()));
        return cantidad + (faltaCreador ? 1 : 0);
    }

    public int cuposDisponibles(GrupoEstudio grupo) {
        return Math.max(0, grupo.getCapacidadMaxima() - integrantesActuales(grupo));
    }

    public boolean publicacionesAbiertas(GrupoEstudio grupo) {
        return grupo.getTipoGrupo() == TipoGrupo.ESTUDIANTES
                || Boolean.TRUE.equals(grupo.getPermitirPublicaciones());
    }

    public boolean puedePublicarRecursos(GrupoEstudio grupo, Usuario usuario) {
        return "ACTIVO".equals(grupo.getEstado())
                && usuario != null
                && esIntegrante(grupo, usuario.getIdUsuario())
                && (esAdministrador(grupo, usuario.getIdUsuario())
                || publicacionesAbiertas(grupo));
    }

    public boolean puedeGestionarTemas(GrupoEstudio grupo, Usuario usuario) {
        return "ACTIVO".equals(grupo.getEstado())
                && usuario != null
                && esIntegrante(grupo, usuario.getIdUsuario())
                && (esAdministrador(grupo, usuario.getIdUsuario())
                || grupo.getTipoGrupo() == TipoGrupo.ESTUDIANTES);
    }
}
