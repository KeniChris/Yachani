package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.TemaGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemaGrupoRepository
        extends JpaRepository<TemaGrupo, Long> {

    //Cuenta los temas de un grupo.
    long countByGrupo_IdGrupo(Long idGrupo);

    //Lista los temas del grupo ordenados por nombre ascendente.
    List<TemaGrupo> findByGrupo_IdGrupoOrderByNombreAsc(
            Long idGrupo
    );

    //Busca un tema dentro de un grupo específico
    Optional<TemaGrupo> findByIdTemaAndGrupo_IdGrupo(
            Long idTema,
            Long idGrupo
    );
}