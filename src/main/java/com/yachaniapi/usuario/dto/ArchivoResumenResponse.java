package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

/**
 * Contiene el archivo y sus datos necesarios para descargarlo.
 */
@Getter
@AllArgsConstructor
public class ArchivoResumenResponse {

    private Resource archivo;
    private String nombreArchivo;
    private String tipoArchivo;
}