package com.yachaniapi.usuario.repository;

import com.yachaniapi.usuario.entity.Resumen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumenRepository
        extends JpaRepository<Resumen, Long> {

    /**
     * Busca todos los resúmenes publicados por un estudiante,mostrando primero los más recientes.
     */
    List<Resumen> findByEstudiante_IdUsuarioOrderByFechaPublicacionDesc(
            Long idUsuario
    );

    /**
     * Busca un resumen específico
     */
    Optional<Resumen> findByIdResumenAndEstudiante_IdUsuario(
            Long idResumen,
            Long idUsuario
    );
}