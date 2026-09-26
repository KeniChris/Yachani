package com.yachaniapi.resena.service;

import com.yachaniapi.resena.dto.PerfilTutorResponse;
import com.yachaniapi.resena.dto.ResenaRequest;
import com.yachaniapi.resena.dto.ResenaResponse;
import com.yachaniapi.resena.entity.Resena;
import com.yachaniapi.resena.exception.ResenaExceptions.*;
import com.yachaniapi.resena.mapper.ResenaMapper;
import com.yachaniapi.resena.repository.ResenaRepository;
import com.yachaniapi.usuario.entity.Estudiante;
import com.yachaniapi.usuario.entity.Tutor;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaService {

    private static final String MENSAJE_TUTOR_NUEVO =
            "Este tutor aún no tiene calificaciones. ¡Sé el primero en calificarlo!";

    private final ResenaRepository resenaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResenaMapper resenaMapper;

    public ResenaService(
            ResenaRepository resenaRepository,
            UsuarioRepository usuarioRepository,
            ResenaMapper resenaMapper) {

        this.resenaRepository = resenaRepository;
        this.usuarioRepository = usuarioRepository;
        this.resenaMapper = resenaMapper;
    }

    @Transactional
    public PerfilTutorResponse consultarPerfilTutor(Long idTutor) {

        Tutor tutor = buscarTutor(idTutor);

        long totalResenas = resenaRepository.countByTutor_IdUsuario(idTutor);

        if (totalResenas == 0) {
            return resenaMapper.toPerfilTutorResponse(
                    tutor, null, 0L, MENSAJE_TUTOR_NUEVO
            );
        }

        Double promedio = redondear(resenaRepository.calcularPromedioPorTutor(idTutor));

        return resenaMapper.toPerfilTutorResponse(
                tutor, promedio, totalResenas, "Perfil del tutor obtenido correctamente"
        );
    }

    @Transactional
    public List<ResenaResponse> listarResenasRelevantes(Long idTutor) {

        buscarTutor(idTutor);

        return resenaRepository
                .findTop10ByTutor_IdUsuarioOrderByCalificacionDescFechaPublicacionDesc(idTutor)
                .stream()
                .map(resenaMapper::toResenaResponse)
                .toList();
    }

    @Transactional
    public ResenaResponse registrarResena(Long idTutor, ResenaRequest request) {

        Tutor tutor = buscarTutor(idTutor);
        Estudiante estudiante = buscarEstudiante(request.getIdEstudiante());

        boolean perteneceAGrupo = resenaRepository
                .estudiantePerteneceAGrupoDelTutor(idTutor, estudiante.getIdUsuario());

        if (!perteneceAGrupo) {
            throw new ResenaNoPermitidaException(
                    "Solo puedes calificar a tutores de grupos a los que perteneces"
            );
        }

        boolean yaReseno = resenaRepository
                .existsByTutor_IdUsuarioAndEstudiante_IdUsuario(idTutor, estudiante.getIdUsuario());

        if (yaReseno) {
            throw new ResenaDuplicadaException("Ya registraste una reseña para este tutor");
        }

        Resena resena = new Resena();
        resena.setTutor(tutor);
        resena.setEstudiante(estudiante);
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario().trim());

        Resena resenaGuardada = resenaRepository.save(resena);

        return resenaMapper.toResenaResponse(resenaGuardada);
    }

    private Tutor buscarTutor(Long idTutor) {

        Usuario usuario = usuarioRepository.findById(idTutor)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Tutor no encontrado"));

        if (!(usuario instanceof Tutor tutor)) {
            throw new UsuarioNoEsTutorException("El usuario indicado no es un tutor");
        }

        return tutor;
    }

    private Estudiante buscarEstudiante(Long idEstudiante) {

        Usuario usuario = usuarioRepository.findById(idEstudiante)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Estudiante no encontrado"));

        if (!(usuario instanceof Estudiante estudiante)) {
            throw new UsuarioNoEsEstudianteException("Solo los estudiantes pueden calificar tutores");
        }

        return estudiante;
    }

    private Double redondear(Double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}