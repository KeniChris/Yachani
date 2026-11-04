package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolicitudGrupoRepository extends JpaRepository<SolicitudGrupo, Long> {

    boolean existsByGrupo_IdGrupoAndEstudiante_IdUsuario(
            Long idGrupo, Long idEstudiante
    );

    Page<SolicitudGrupo> findByGrupo_IdGrupoAndEstado(
            Long idGrupo, String estado, Pageable pageable
    );

    Page<SolicitudGrupo> findByEstudiante_IdUsuarioAndEstado(
            Long idEstudiante, String estado, Pageable pageable
    );

    // Obtiene solo el ID: la entidad se carga después de bloquear el grupo.
    @Query("select s.grupo.idGrupo from SolicitudGrupo s where s.idSolicitud = :idSolicitud")
    Optional<Long> buscarIdGrupo(@Param("idSolicitud") Long idSolicitud);
}
