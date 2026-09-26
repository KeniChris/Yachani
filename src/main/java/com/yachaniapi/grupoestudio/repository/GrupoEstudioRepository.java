package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, Long> {

    @Query("SELECT g FROM GrupoEstudio g WHERE LOWER(g.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<GrupoEstudio> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT g FROM GrupoEstudio g WHERE " +
           "(:curso IS NULL OR LOWER(g.nombre) LIKE LOWER(CONCAT('%', :curso, '%'))) AND " +
           "(:metodoEstudio IS NULL OR LOWER(g.metodoEstudio) = LOWER(:metodoEstudio)) AND " +
           "(:cantidadIntegrantes IS NULL OR SIZE(g.participantes) = :cantidadIntegrantes)")
    List<GrupoEstudio> findByFiltros(
            @Param("curso") String curso,
            @Param("metodoEstudio") String metodoEstudio,
            @Param("cantidadIntegrantes") Integer cantidadIntegrantes);
}
