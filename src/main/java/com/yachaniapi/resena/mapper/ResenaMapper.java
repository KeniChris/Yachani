package com.yachaniapi.resena.mapper;

import com.yachaniapi.resena.dto.PerfilTutorResponse;
import com.yachaniapi.resena.dto.ResenaResponse;
import com.yachaniapi.resena.entity.Resena;
import com.yachaniapi.usuario.entity.Tutor;
import org.springframework.stereotype.Component;

@Component
public class ResenaMapper {

    public ResenaResponse toResenaResponse(Resena resena) {

        String nombreEstudiante = resena.getEstudiante().getNombres()
                + " " + resena.getEstudiante().getApellidos();

        return new ResenaResponse(
                resena.getIdResena(),
                nombreEstudiante,
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFechaPublicacion()
        );
    }

    public PerfilTutorResponse toPerfilTutorResponse(
            Tutor tutor,
            Double promedio,
            Long totalResenas,
            String mensaje) {

        return new PerfilTutorResponse(
                tutor.getIdUsuario(),
                tutor.getNombres(),
                tutor.getApellidos(),
                tutor.getCorreo(),
                promedio,
                totalResenas,
                mensaje
        );
    }
}