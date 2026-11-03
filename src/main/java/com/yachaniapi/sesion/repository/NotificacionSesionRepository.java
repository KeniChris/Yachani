package com.yachaniapi.sesion.repository;

import com.yachaniapi.sesion.entity.NotificacionSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionSesionRepository extends JpaRepository<NotificacionSesion, Long> {

    List<NotificacionSesion> findByEstudiante_IdUsuarioAndSesion_EstadoOrderByFechaCreacionDesc(
            Long idEstudiante, String estado);

    void deleteBySesion_IdSesionAndTipo(Long idSesion, String tipo);
}