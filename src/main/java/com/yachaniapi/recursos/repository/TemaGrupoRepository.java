package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.TemaGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemaGrupoRepository
        extends JpaRepository<TemaGrupo, Long> {

    long countByGrupo_IdGrupo(Long idGrupo);

    List<TemaGrupo> findByGrupo_IdGrupoOrderByNombreAsc(
            Long idGrupo
    );

    Optional<TemaGrupo> findByIdTemaAndGrupo_IdGrupo(
            Long idTema,
            Long idGrupo
    );
}