package com.yachaniapi.recursos.service;

import com.yachaniapi.recursos.dto.RecursosDTO.DescargaResponse;
import com.yachaniapi.recursos.entity.MaterialEstudio;
import com.yachaniapi.recursos.exception.RecursosException;
import com.yachaniapi.recursos.service.ValidadorMaterialService.ArchivoValidado;

public interface AlmacenamientoMaterialService {

    String nuevoId(Long idGrupo, String extension);

    void subir(
            String idArchivo,
            ArchivoValidado archivo
    ) throws RecursosException;

    DescargaResponse descarga(MaterialEstudio material);

    void eliminar(
            String idArchivo,
            String tipoRecurso,
            String tipoAcceso
    );
}