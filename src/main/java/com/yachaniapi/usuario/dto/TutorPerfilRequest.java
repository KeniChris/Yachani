package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorPerfilRequest {
    @Size(max = 120)
    private String universidad;
    @Size(max = 120)
    private String carrera;


    @NotBlank(message = "La presentación es obligatoria")
    @Size(max = 500, message = "La presentación no puede superar los 500 caracteres")
    private String presentacion;

    @NotBlank(message = "El método de enseñanza es obligatorio")
    @Size(max = 50)
    private String metodoEnsenanza;
}