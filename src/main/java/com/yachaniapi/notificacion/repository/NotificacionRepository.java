package com.yachaniapi.notificacion.repository;

import com.yachaniapi.notificacion.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    Page<Notificacion> findByDestinatario_IdUsuario(Long idUsuario, Pageable pageable);
    List<Notificacion> findByDestinatario_IdUsuarioOrderByFechaCreacionDesc(Long idUsuario);
    Optional<Notificacion> findByIdNotificacionAndDestinatario_IdUsuario(Long idNotificacion, Long idUsuario);
    long countByDestinatario_IdUsuarioAndLeidaFalse(Long idUsuario);
    List<Notificacion> findByDestinatario_IdUsuarioAndSesionIsNotNullOrderByFechaCreacionDesc(Long idUsuario);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notificacion n set n.leida = true where n.destinatario.idUsuario = :id and n.leida = false")
    int marcarTodasLeidas(@Param("id") Long idUsuario);

    void deleteBySesion_IdSesionAndTipoAndLeidaFalse(Long idSesion, String tipo);
}
