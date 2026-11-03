package com.yachaniapi.usuario.repository;

import com.yachaniapi.usuario.entity.CodigoRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CodigoRecuperacionRepository
        extends JpaRepository<CodigoRecuperacion, Long> {

    Optional<CodigoRecuperacion>
    findTopByUsuario_IdUsuarioAndUtilizadoFalseOrderByIdCodigoDesc(
            Long idUsuario
    );

    void deleteByUsuario_IdUsuario(Long idUsuario);
}