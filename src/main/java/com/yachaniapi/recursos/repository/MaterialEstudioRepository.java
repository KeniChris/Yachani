package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.MaterialEstudio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialEstudioRepository
        extends JpaRepository<MaterialEstudio, Long> {

    //Obtiene una página de materiales del grupo.
    Page<MaterialEstudio> findByGrupo_IdGrupo(
            Long idGrupo,
            Pageable pageable
    );

    //Obtiene materiales del grupo filtrados por tema
    Page<MaterialEstudio> findByGrupo_IdGrupoAndTema_IdTema(
            Long idGrupo,
            Long idTema,
            Pageable pageable
    );
    //Busca un material dentro del grupo indicado.
    Optional<MaterialEstudio> findByIdMaterialAndGrupo_IdGrupo(
            Long idMaterial,
            Long idGrupo
    );
}