package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RecuperarContrasenaRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@upc\\.edu\\.pe$",
            message = "El correo debe pertenecer al dominio @upc.edu.pe"
    )
    private String correo;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}