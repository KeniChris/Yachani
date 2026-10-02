package com.yachaniapi.usuario.repository;

import com.yachaniapi.usuario.entity.ConocimientoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * se bbusca listar, buscar y evitar que un estudiante registre el mismo curso dos veces
 * acceso a los cursos y niveles registrados por los estudiantes.
 */
@Repository
public interface ConocimientoAcademicoRepository
        extends JpaRepository<ConocimientoAcademico, Long> {

    /**
     * Obtiene los conocimientos de un estudiante,
     * ordenados alfabéticamente por curso.
     */
    List<ConocimientoAcademico>
    findByEstudiante_IdUsuarioOrderByCursoAsc(Long idUsuario);

    /**
     * Busca un conocimiento específico y comprueba
     * que pertenezca al estudiante indicado.
     */
    Optional<ConocimientoAcademico>
    findByIdConocimientoAndEstudiante_IdUsuario(
            Long idConocimiento,
            Long idUsuario
    );

    /**
     * Comprueba si el estudiante ya registró el curso.
     */
    boolean existsByEstudiante_IdUsuarioAndCursoIgnoreCase(
            Long idUsuario,
            String curso
    );
}