package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.entity.*;
import com.yachaniapi.usuario.repository.ConocimientoAcademicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstadoConfiguracionService {
    private final ConocimientoAcademicoRepository conocimientos;

    public void actualizar(Usuario usuario) {
        boolean completa = false;
        if (usuario instanceof Estudiante estudiante) {
            var cursos = conocimientos.findByEstudiante_IdUsuarioOrderByCursoAsc(usuario.getIdUsuario());
            completa = tieneTexto(estudiante.getUniversidad()) && tieneTexto(estudiante.getCarrera())
                    && tieneTexto(estudiante.getModalidadPreferida())
                    && tieneTexto(estudiante.getMetodoPreferido()) && !cursos.isEmpty()
                    && cursos.stream().allMatch(c -> tieneTexto(c.getCurso()) && tieneTexto(c.getNivel()));
        } else if (usuario instanceof Tutor tutor) {
            completa = tieneTexto(tutor.getPresentacion()) && tieneTexto(tutor.getMetodoEnsenanza());
        }
        usuario.setConfiguracionCompletada(completa);
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
