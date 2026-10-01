package com.yachaniapi.sesion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponse {

    private Long idSesion;
    private Long idGrupo;
    private String nombreGrupo;
    private String tema;
    private String descripcion;
    private LocalDateTime fechaHoraInicio;
    private Integer duracionMinutos;
    private String lugar;
    private String estado;
    private String mensaje;
}