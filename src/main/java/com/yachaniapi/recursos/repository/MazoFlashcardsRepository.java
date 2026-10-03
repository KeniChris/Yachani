package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.MazoFlashcards;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MazoFlashcardsRepository
        extends JpaRepository<MazoFlashcards, Long> {

    List<MazoFlashcards> findByCreador_IdUsuarioOrderByIdMazoDesc(
            Long idUsuario
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m
            from MazoFlashcards m
            where m.idMazo = :id
            """)
    Optional<MazoFlashcards> buscarConBloqueo(
            @Param("id") Long id
    );
}