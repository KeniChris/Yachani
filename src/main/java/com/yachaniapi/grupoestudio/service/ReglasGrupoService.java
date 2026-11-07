package com.yachaniapi.grupoestudio.service;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

@Service
public class ReglasGrupoService {

    // En datos anteriores tipo_grupo puede no coincidir con el creador.
    // Se resuelve la clase real incluso si JPA devuelve un proxy de Usuario.
    public TipoGrupo tipoGrupo(GrupoEstudio grupo) {
        Object creador = Hibernate.unproxy(grupo.getCreador());
        if (creador instanceof Tutor) return TipoGrupo.TUTOR;
        if (creador instanceof Estudiante) return TipoGrupo.ESTUDIANTES;
        return grupo.getTipoGrupo();
    }

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
        boolean conTutor = tipoGrupo(grupo) == TipoGrupo.TUTOR;
        Long idCreador = grupo.getCreador().getIdUsuario();
        int cantidad = (int) grupo.getParticipantes().stream()
                .map(Usuario::getIdUsuario)
                .filter(id -> !conTutor || !idCreador.equals(id))
                .distinct().count();
        boolean faltaCreador = !conTutor
                && grupo.getParticipantes().stream().noneMatch(p ->
                grupo.getCreador().getIdUsuario().equals(p.getIdUsuario()));
        return cantidad + (faltaCreador ? 1 : 0);
    }

    public int cuposDisponibles(GrupoEstudio grupo) {
        return Math.max(0, grupo.getCapacidadMaxima() - integrantesActuales(grupo));
    }

    public boolean publicacionesAbiertas(GrupoEstudio grupo) {
        return tipoGrupo(grupo) == TipoGrupo.ESTUDIANTES
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
                || tipoGrupo(grupo) == TipoGrupo.ESTUDIANTES);
    }
}
