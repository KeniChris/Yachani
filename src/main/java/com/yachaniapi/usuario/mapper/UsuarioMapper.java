package com.yachaniapi.usuario.mapper;

import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    /**
     * Convierte los datos del usuario en la respuesta que recibirá el frontend.
     */
    public UsuarioResponse toResponse(
            Usuario usuario,
            String mensaje) {

        String tipoUsuario = usuario instanceof Estudiante
                ? "ESTUDIANTE"
                : "TUTOR";

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                tipoUsuario,
                mensaje
        );
    }
}