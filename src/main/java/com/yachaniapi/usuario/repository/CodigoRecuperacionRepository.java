package com.yachaniapi.usuario.repository;

import com.yachaniapi.usuario.entity.CodigoRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoRecuperacionRepository
        extends JpaRepository<CodigoRecuperacion, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Optional<CodigoRecuperacion>
    findTopByUsuario_IdUsuarioAndUtilizadoFalseOrderByIdCodigoDesc(
            Long idUsuario
    );

    void deleteByUsuario_IdUsuario(Long idUsuario);
}