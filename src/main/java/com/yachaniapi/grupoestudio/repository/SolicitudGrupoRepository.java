package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudGrupoRepository extends JpaRepository<SolicitudGrupo, Long> {

    boolean existsByGrupo_IdGrupoAndEstudiante_IdUsuario(Long idGrupo, Long idEstudiante);

    // Solo bloquea una solicitud nueva si ya hay una pendiente: si la anterior fue
    // rechazada, o fue aceptada y luego al estudiante lo sacaron del grupo, puede
    // volver a solicitar.
    boolean existsByGrupo_IdGrupoAndEstudiante_IdUsuarioAndEstado(
            Long idGrupo, Long idEstudiante, String estado);

    List<SolicitudGrupo> findByGrupo_IdGrupoAndEstadoOrderByFechaSolicitudAsc(Long idGrupo, String estado);

    // Solicitudes que envió un estudiante, para la sección "Mis solicitudes".
    List<SolicitudGrupo> findByEstudiante_IdUsuarioOrderByFechaSolicitudDesc(Long idEstudiante);
}
