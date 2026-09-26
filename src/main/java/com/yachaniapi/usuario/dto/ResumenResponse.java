package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResumenResponse {

    private Long idResumen;
    private String titulo;
    private String nombreArchivo;
    private String tipoArchivo;
    private Long tamanoArchivo;
    private LocalDateTime fechaPublicacion;
}