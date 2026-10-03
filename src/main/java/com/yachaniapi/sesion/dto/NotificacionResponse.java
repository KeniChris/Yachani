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
public class NotificacionResponse {

    private Long idNotificacion;
    private Long idSesion;
    private String tipo;
    private String titulo;
    private String grupo;
    private String tema;
    private String fecha;
    private String hora;
    private String lugar;
    private LocalDateTime fechaCreacion;
}