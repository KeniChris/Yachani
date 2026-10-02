package com.yachaniapi.recursos.mapper;

import com.yachaniapi.recursos.dto.RecursosDTO.MaterialResponse;
import com.yachaniapi.recursos.dto.RecursosDTO.TemaResponse;
import com.yachaniapi.recursos.entity.MaterialEstudio;
import com.yachaniapi.recursos.entity.TemaGrupo;
import org.springframework.stereotype.Component;

@Component
public class RecursosMapper {

    public TemaResponse tema(TemaGrupo tema) {
        return new TemaResponse(
                tema.getIdTema(),
                tema.getGrupo().getIdGrupo(),
                tema.getNombre(),
                tema.getDescripcion(),
                tema.getCreador().getIdUsuario(),
                tema.getFechaCreacion()
        );
    }

    public MaterialResponse material(MaterialEstudio material) {
        TemaGrupo tema = material.getTema();

        return new MaterialResponse(
                material.getIdMaterial(),
                material.getGrupo().getIdGrupo(),
                tema == null ? null : tema.getIdTema(),
                tema == null ? null : tema.getNombre(),
                material.getAutor().getIdUsuario(),
                material.getAutor().getNombres()
                        + " "
                        + material.getAutor().getApellidos(),
                material.getNombreArchivo(),
                material.getTipoArchivo(),
                material.getTamanoArchivo(),
                material.getFechaPublicacion()
        );
    }
}