package com.yachaniapi.recursos.repository;

import com.yachaniapi.recursos.entity.ProgresoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgresoTarjetaRepository
        extends JpaRepository<ProgresoTarjeta, Long> {

    Optional<ProgresoTarjeta>
    findByUsuario_IdUsuarioAndTarjeta_IdTarjeta(
            Long idUsuario,
            Long idTarjeta
    );

    List<ProgresoTarjeta>
    findByUsuario_IdUsuarioAndTarjeta_Mazo_IdMazo(
            Long idUsuario,
            Long idMazo
    );
}