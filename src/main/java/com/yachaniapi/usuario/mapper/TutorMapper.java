package com.yachaniapi.usuario.mapper;

import com.yachaniapi.usuario.dto.TutorPerfilResponse;
import com.yachaniapi.usuario.entity.Tutor;
import org.springframework.stereotype.Component;

@Component
public class TutorMapper {

    public TutorPerfilResponse toResponse(Tutor tutor) {
        return new TutorPerfilResponse(
                tutor.getIdUsuario(),
                tutor.getNombres(),
                tutor.getApellidos(),
                tutor.getCorreo(),
                tutor.getPresentacion(),
                tutor.getMetodoEnsenanza()
        );
    }
}