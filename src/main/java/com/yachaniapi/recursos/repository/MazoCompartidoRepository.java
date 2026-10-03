package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.EstadoMazo;
import com.yachaniapi.recursos.entity.MazoCompartido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MazoCompartidoRepository
        extends JpaRepository<MazoCompartido, Long> {

    Optional<MazoCompartido>
    findByMazo_IdMazoAndGrupo_IdGrupo(
            Long idMazo,
            Long idGrupo
    );

    List<MazoCompartido> findByMazo_IdMazo(
            Long idMazo
    );

    List<MazoCompartido>
    findByGrupo_IdGrupoAndMazo_EstadoOrderByIdCompartidoDesc(
            Long idGrupo,
            EstadoMazo estado
    );
}