package com.yachaniapi.grupoestudio.repository;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.entity.TipoGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, Long> {

    long countByCreador_IdUsuario(Long idUsuario);

    @Query("SELECT g FROM GrupoEstudio g WHERE LOWER(g.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<GrupoEstudio> findByKeyword(@Param("keyword") String keyword);

    // Solo los filtros que la base puede resolver sin comparar texto. El curso y el método
    // de estudio se filtran en el servicio, que normaliza tildes y mayúsculas; hacerlo aquí
    // obligaría a LOWER(?) sobre un parámetro nulo, que en PostgreSQL falla con
    // "function lower(bytea) does not exist".
    @Query("SELECT g FROM GrupoEstudio g WHERE " +
           "(:cantidadIntegrantes IS NULL OR SIZE(g.participantes) = :cantidadIntegrantes) AND " +
           "(:tipoGrupo IS NULL OR g.tipoGrupo = :tipoGrupo)")
    List<GrupoEstudio> findByFiltros(
            @Param("cantidadIntegrantes") Integer cantidadIntegrantes,
            @Param("tipoGrupo") TipoGrupo tipoGrupo);

    @Query("SELECT DISTINCT g FROM GrupoEstudio g LEFT JOIN g.participantes p " +
           "WHERE g.creador.idUsuario = :idUsuario OR p.idUsuario = :idUsuario")
    List<GrupoEstudio> findByUsuario(@Param("idUsuario") Long idUsuario);
}
