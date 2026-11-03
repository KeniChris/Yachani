package com.yachaniapi.aviso.repository;

import com.yachaniapi.aviso.entity.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    List<Aviso> findByGrupo_IdGrupoOrderByFechaPublicacionDesc(Long idGrupo);

    Optional<Aviso> findByIdAvisoAndGrupo_IdGrupo(Long idAviso, Long idGrupo);
}
