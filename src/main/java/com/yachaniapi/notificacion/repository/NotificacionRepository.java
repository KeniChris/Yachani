package com.yachaniapi.notificacion.repository;

import com.yachaniapi.notificacion.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByDestinatario_IdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    Optional<Notificacion> findByIdNotificacionAndDestinatario_IdUsuario(Long idNotificacion, Long idUsuario);
}
