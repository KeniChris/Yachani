package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.TarjetaFlashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarjetaFlashcardRepository
        extends JpaRepository<TarjetaFlashcard, Long> {

    List<TarjetaFlashcard>
    findByMazo_IdMazoAndActivaTrueOrderByOrdenAscIdTarjetaAsc(
            Long idMazo
    );

    Optional<TarjetaFlashcard>
    findByIdTarjetaAndMazo_IdMazoAndActivaTrue(
            Long idTarjeta,
            Long idMazo
    );

    long countByMazo_IdMazoAndActivaTrue(
            Long idMazo
    );

    @Query("""
            select coalesce(max(t.orden), 0)
            from TarjetaFlashcard t
            where t.mazo.idMazo = :id
            """)
    int ultimoOrden(
            @Param("id") Long id
    );
}