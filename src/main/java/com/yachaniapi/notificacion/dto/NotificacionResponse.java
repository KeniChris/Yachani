package com.yachaniapi.notificacion.dto;

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
    private Long idSolicitud;
    private Long idGrupo;
    private String nombreGrupo;
    private String tipo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;
}
