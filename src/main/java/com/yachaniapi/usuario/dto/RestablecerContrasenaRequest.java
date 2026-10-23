package com.yachaniapi.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RestablecerContrasenaRequest {

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "El código es obligatorio")
    @Pattern(
            regexp = "\\d{6}",
            message = "El código debe contener 6 números"
    )
    private String codigo;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(
            min = 8,
            message = "La contraseña debe tener como mínimo 8 caracteres"
    )
    private String nuevaContrasena;

    @NotBlank(message = "La confirmación de la contraseña es obligatoria")
    private String confirmarContrasena;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
}