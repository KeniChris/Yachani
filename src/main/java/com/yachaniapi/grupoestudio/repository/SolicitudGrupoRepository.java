package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.SolicitudGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudGrupoRepository extends JpaRepository<SolicitudGrupo, Long> {

    boolean existsByGrupo_IdGrupoAndEstudiante_IdUsuario(Long idGrupo, Long idEstudiante);

    List<SolicitudGrupo> findByGrupo_IdGrupoAndEstadoOrderByFechaSolicitudAsc(Long idGrupo, String estado);
}
