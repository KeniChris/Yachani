package com.yachaniapi.usuario.service;

import com.yachaniapi.usuario.dto.TutorPerfilRequest;
import com.yachaniapi.usuario.dto.TutorPerfilResponse;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsTutorException;
import com.yachaniapi.usuario.mapper.TutorMapper;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Gestiona la consulta y actualización del perfil del tutor
 */
@Service
public class TutorService {

    private final UsuarioRepository usuarioRepository;
    private final TutorMapper tutorMapper;

    public TutorService(
            UsuarioRepository usuarioRepository,
            TutorMapper tutorMapper) {

        this.usuarioRepository = usuarioRepository;
        this.tutorMapper = tutorMapper;
    }

    /**
     * Consulta la información del perfil del tutor
     */
    @Transactional
    public TutorPerfilResponse consultarPerfil(Long idUsuario) {

        Tutor tutor = buscarTutor(idUsuario);

        return tutorMapper.toResponse(tutor);
    }

    /**
     * Actualiza la presentación y el metodo de enseñanza del tutor.
     */
    @Transactional
    public TutorPerfilResponse actualizarPerfil(
            Long idUsuario,
            TutorPerfilRequest request) {

        Tutor tutor = buscarTutor(idUsuario);

        tutor.setPresentacion(
                request.getPresentacion().trim()
        );

        tutor.setMetodoEnsenanza(
                request.getMetodoEnsenanza().trim()
        );

        Tutor tutorGuardado = usuarioRepository.save(tutor);

        return tutorMapper.toResponse(tutorGuardado);
    }

    /**
     * Busca al usuario y comprueba que sea un tutor
     */
    private Tutor buscarTutor(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new UsuarioNoEncontradoException(
                                "Usuario no encontrado"
                        )
                );

        if (!(usuario instanceof Tutor tutor)) {
            throw new UsuarioNoEsTutorException(
                    "El usuario indicado no es un tutor"
            );
        }

        return tutor;
    }
}