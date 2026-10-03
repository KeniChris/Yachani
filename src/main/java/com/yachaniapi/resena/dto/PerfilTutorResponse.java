package com.yachaniapi.resena.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilTutorResponse {

    private Long idTutor;
    private String nombres;
    private String apellidos;
    private String correo;
    private Double promedioCalificacion;
    private Long totalResenas;
    private String mensaje;
}