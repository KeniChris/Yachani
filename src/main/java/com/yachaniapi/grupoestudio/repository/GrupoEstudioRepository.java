package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GrupoEstudioRepository extends
        JpaRepository<GrupoEstudio, Long>, JpaSpecificationExecutor<GrupoEstudio> {

    long countByCreador_IdUsuario(Long idUsuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GrupoEstudio g where g.idGrupo = :idGrupo")
    Optional<GrupoEstudio> buscarConBloqueo(@Param("idGrupo") Long idGrupo);

    // Serializa el límite de grupos creados por un mismo usuario.
    @Query(value = """
            select id_usuario from usuarios
            where id_usuario = :idUsuario for update
            """, nativeQuery = true)
    Optional<Long> bloquearCreador(@Param("idUsuario") Long idUsuario);
}
