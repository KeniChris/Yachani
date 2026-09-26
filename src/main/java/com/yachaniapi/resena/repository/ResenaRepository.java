package com.yachaniapi.resena.repository;

import com.yachaniapi.resena.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Consultas a la base de datos para obtener promedios, conteos y reseñas de un tutor
@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findTop10ByTutor_IdUsuarioOrderByCalificacionDescFechaPublicacionDesc(Long idTutor);

    long countByTutor_IdUsuario(Long idTutor);

    boolean existsByTutor_IdUsuarioAndEstudiante_IdUsuario(Long idTutor, Long idEstudiante);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.tutor.idUsuario = :idTutor")
    Double calcularPromedioPorTutor(@Param("idTutor") Long idTutor);

    @Query("SELECT COUNT(g) > 0 FROM GrupoEstudio g JOIN g.participantes p " +
           "WHERE g.creador.idUsuario = :idTutor AND p.idUsuario = :idEstudiante")
    boolean estudiantePerteneceAGrupoDelTutor(@Param("idTutor") Long idTutor,
                                              @Param("idEstudiante") Long idEstudiante);
}