package com.yachaniapi.usuario.mapper;

import com.yachaniapi.usuario.dto.ResumenResponse;
import com.yachaniapi.usuario.entity.Resumen;
import org.springframework.stereotype.Component;

@Component
public class ResumenMapper {

    /**
     * Convierte la entidad Resumen en la respuesta para el frontend
     */
    public ResumenResponse toResponse(Resumen resumen) {

        return new ResumenResponse(
                resumen.getIdResumen(),
                resumen.getTitulo(),
                resumen.getNombreArchivo(),
                resumen.getTipoArchivo(),
                resumen.getTamanoArchivo(),
                resumen.getFechaPublicacion()
        );
    }
}