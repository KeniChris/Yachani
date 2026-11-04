package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 72)
    private String contrasena;
}
