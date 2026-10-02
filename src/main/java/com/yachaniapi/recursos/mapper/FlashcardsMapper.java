package com.yachaniapi.recursos.mapper;

import com.yachaniapi.recursos.dto.response.*;
import com.yachaniapi.recursos.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FlashcardsMapper {

    public MazoResponse mazo(
            MazoFlashcards mazo,
            long cantidad
    ) {
        var autor = mazo.getCreador();

        return new MazoResponse(
                mazo.getIdMazo(),
                autor.getIdUsuario(),
                autor.getNombres() + " " + autor.getApellidos(),
                mazo.getTitulo(),
                mazo.getDescripcion(),
                mazo.getEstado(),
                cantidad,
                mazo.getFechaCreacion()
        );
    }

    public TarjetaResponse tarjeta(
            TarjetaFlashcard tarjeta
    ) {
        return new TarjetaResponse(
                tarjeta.getIdTarjeta(),
                tarjeta.getPregunta(),
                tarjeta.getRespuesta(),
                tarjeta.getOrden()
        );
    }

    public MazoDetalleResponse detalle(
            MazoFlashcards mazo,
            List<TarjetaFlashcard> tarjetas
    ) {
        return new MazoDetalleResponse(
                mazo(mazo, tarjetas.size()),
                tarjetas.stream()
                        .map(this::tarjeta)
                        .toList()
        );
    }

    public MazoCompartidoResponse compartido(
            MazoCompartido compartido,
            long cantidad
    ) {
        return new MazoCompartidoResponse(
                compartido.getIdCompartido(),
                compartido.getGrupo().getIdGrupo(),
                compartido.getTema().getIdTema(),
                compartido.getTema().getNombre(),
                mazo(compartido.getMazo(), cantidad),
                compartido.getFechaCompartido()
        );
    }

    public ProgresoResponse progreso(
            Long idMazo,
            List<TarjetaFlashcard> tarjetas,
            List<ProgresoTarjeta> progresos
    ) {
        Map<Long, ProgresoTarjeta> porTarjeta =
                progresos.stream().collect(
                        Collectors.toMap(
                                progreso ->
                                        progreso.getTarjeta().getIdTarjeta(),
                                Function.identity()
                        )
                );

        List<EstadoTarjetaResponse> estados =
                tarjetas.stream().map(tarjeta -> {

                    var progreso =
                            porTarjeta.get(tarjeta.getIdTarjeta());

                    return new EstadoTarjetaResponse(
                            tarjeta.getIdTarjeta(),
                            progreso != null && progreso.isVista(),
                            progreso != null && progreso.isAprendida(),
                            progreso == null
                                    ? null
                                    : progreso.getFechaUltimaPractica()
                    );
                }).toList();

        long vistas = estados.stream()
                .filter(EstadoTarjetaResponse::vista)
                .count();

        long aprendidas = estados.stream()
                .filter(EstadoTarjetaResponse::aprendida)
                .count();

        long total = estados.size();

        double porcentaje = total == 0
                ? 0
                : Math.round(vistas * 10000.0 / total) / 100.0;

        Long siguiente = estados.stream()
                .filter(estado -> !estado.vista())
                .map(EstadoTarjetaResponse::idTarjeta)
                .findFirst()
                .orElse(null);

        return new ProgresoResponse(
                idMazo,
                total,
                vistas,
                aprendidas,
                porcentaje,
                total > 0 && vistas == total,
                siguiente,
                estados
        );
    }
}