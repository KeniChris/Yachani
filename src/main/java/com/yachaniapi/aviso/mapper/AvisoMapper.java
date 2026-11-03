package com.yachaniapi.aviso.mapper;

import com.yachaniapi.aviso.dto.AvisoDTO;
import com.yachaniapi.aviso.entity.Aviso;
import org.springframework.stereotype.Component;

@Component
public class AvisoMapper {

    public AvisoDTO.Response toResponse(Aviso aviso) {

        String nombreAutor = aviso.getAutor().getNombres() + " " + aviso.getAutor().getApellidos();

        return new AvisoDTO.Response(
                aviso.getIdAviso(),
                aviso.getGrupo().getIdGrupo(),
                aviso.getAutor().getIdUsuario(),
                nombreAutor,
                aviso.getTitulo(),
                aviso.getMensaje(),
                aviso.getFechaPublicacion()
        );
    }
}
