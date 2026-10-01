package com.yachaniapi.sesion.mapper;

import com.yachaniapi.sesion.dto.SesionResponse;
import com.yachaniapi.sesion.entity.SesionEstudio;
import org.springframework.stereotype.Component;

@Component
public class SesionEstudioMapper {

    public SesionResponse toSesionResponse(SesionEstudio sesion, String mensaje) {

        return new SesionResponse(
                sesion.getIdSesion(),
                sesion.getGrupo().getIdGrupo(),
                sesion.getGrupo().getNombre(),
                sesion.getTema(),
                sesion.getDescripcion(),
                sesion.getFechaHoraInicio(),
                sesion.getDuracionMinutos(),
                sesion.getLugar(),
                sesion.getEstado(),
                mensaje
        );
    }
}