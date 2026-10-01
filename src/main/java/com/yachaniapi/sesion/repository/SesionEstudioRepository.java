package com.yachaniapi.sesion.repository;

import com.yachaniapi.sesion.entity.SesionEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionEstudioRepository extends JpaRepository<SesionEstudio, Long> {

    List<SesionEstudio> findByGrupo_IdGrupoAndEstadoOrderByFechaHoraInicioAsc(
            Long idGrupo, String estado);

    Optional<SesionEstudio> findByIdSesionAndGrupo_IdGrupo(Long idSesion, Long idGrupo);

    List<SesionEstudio> findByEstadoAndRecordatorioEnviadoFalseAndFechaHoraInicioBetween(
            String estado, LocalDateTime desde, LocalDateTime hasta);
}