package com.yachaniapi.resena.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {

    private Long idResena;
    private String nombreEstudiante;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaPublicacion;
}