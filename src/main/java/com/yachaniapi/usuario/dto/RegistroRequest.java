package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroRequest {
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres admiten hasta 80 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos admiten hasta 80 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo admite hasta 120 caracteres")
    @Pattern(regexp = "(?i)^[A-Za-z0-9._%+-]+@upc\\.edu\\.pe$",
            message = "El correo debe pertenecer al dominio @upc.edu.pe")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña admite entre 8 y 72 caracteres")
    private String contrasena;

    @NotBlank(message = "El tipo de usuario es obligatorio")
    @Pattern(regexp = "(?i)ESTUDIANTE|TUTOR", message = "El tipo de usuario debe ser ESTUDIANTE o TUTOR")
    private String tipoUsuario;
}
