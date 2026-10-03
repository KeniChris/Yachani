package com.yachaniapi.recursos.repository;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GrupoRecursosRepository
        extends JpaRepository<GrupoEstudio, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select g
            from GrupoEstudio g
            where g.idGrupo = :idGrupo
            """)
    Optional<GrupoEstudio> buscarConBloqueo(
            @Param("idGrupo") Long idGrupo
    );
}