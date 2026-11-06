package com.yachaniapi.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TutorPerfilResponse {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String presentacion;
    private String metodoEnsenanza;
    private String universidad;
    private String carrera;
    private boolean configuracionCompletada;
}