package com.yachaniapi.contacto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactoRequest(
        @NotBlank(message = "Ingresa tu nombre")
        @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
        @Pattern(regexp = "[^\\r\\n]*", message = "El nombre debe ocupar una sola línea")
        String nombre,

        @NotBlank(message = "Ingresa tu correo")
        @Email(message = "Ingresa un correo electrónico válido")
        @Size(max = 254, message = "El correo admite hasta 254 caracteres")
        @Pattern(regexp = "[^\\r\\n]*", message = "Ingresa un correo electrónico válido")
        String correo,

        @NotBlank(message = "Ingresa el asunto")
        @Size(max = 150, message = "El asunto admite hasta 150 caracteres")
        @Pattern(regexp = "[^\\r\\n]*", message = "El asunto debe ocupar una sola línea")
        String asunto,

        @NotBlank(message = "Escribe tu mensaje")
        @Size(max = 5000, message = "El mensaje admite hasta 5000 caracteres")
        String mensaje) {

    public ContactoRequest {
        nombre = limpiar(nombre);
        correo = limpiar(correo);
        asunto = limpiar(asunto);
        mensaje = limpiar(mensaje);
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.strip();
    }
}
