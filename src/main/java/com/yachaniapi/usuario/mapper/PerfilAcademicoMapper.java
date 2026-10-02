package com.yachaniapi.usuario.mapper;

import com.yachaniapi.usuario.dto.ConocimientoAcademicoResponse;
import com.yachaniapi.usuario.dto.PerfilAcademicoResponse;
import com.yachaniapi.usuario.entity.ConocimientoAcademico;
import com.yachaniapi.usuario.entity.Estudiante;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerfilAcademicoMapper {

    /**
     * Convierte el perfil del estudiante en la respuesta para el frontend.
     */
    public PerfilAcademicoResponse toPerfilResponse(
            Estudiante estudiante,
            List<ConocimientoAcademico> conocimientos) {

        List<ConocimientoAcademicoResponse> conocimientosResponse =
                conocimientos.stream()
                        .map(this::toConocimientoResponse)
                        .toList();

        return new PerfilAcademicoResponse(
                estudiante.getIdUsuario(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getCorreo(),
                estudiante.getUniversidad(),
                estudiante.getCarrera(),
                estudiante.getModalidadPreferida(),
                estudiante.getMetodoPreferido(),
                new ArrayList<>(estudiante.getTemasInteres()),
                conocimientosResponse
        );
    }

    /**
     * Convierte un curso registrado en una respuesta.
     */
    public ConocimientoAcademicoResponse toConocimientoResponse(
            ConocimientoAcademico conocimiento) {

        return new ConocimientoAcademicoResponse(
                conocimiento.getIdConocimiento(),
                conocimiento.getCurso(),
                conocimiento.getNivel()
        );
    }
}