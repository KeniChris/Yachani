package com.yachaniapi.chat.service;

import com.yachaniapi.chat.dto.EnviarMensajeRequest;
import com.yachaniapi.chat.dto.MensajeChatResponse;
import com.yachaniapi.chat.entity.MensajeChat;
import com.yachaniapi.chat.exception.ChatExceptions.*;
import com.yachaniapi.chat.mapper.MensajeChatMapper;
import com.yachaniapi.chat.repository.MensajeChatRepository;
import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.grupoestudio.exception.GrupoEstudioExceptions.GrupoNoEncontradoException;
import com.yachaniapi.grupoestudio.repository.GrupoEstudioRepository;
import com.yachaniapi.usuario.entity.Usuario;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private static final long MINUTOS_PARA_ELIMINAR = 2;

    private final MensajeChatRepository mensajeChatRepository;
    private final GrupoEstudioRepository grupoEstudioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensajeChatMapper mensajeChatMapper;

    public ChatService(
            MensajeChatRepository mensajeChatRepository,
            GrupoEstudioRepository grupoEstudioRepository,
            UsuarioRepository usuarioRepository,
            MensajeChatMapper mensajeChatMapper) {

        this.mensajeChatRepository = mensajeChatRepository;
        this.grupoEstudioRepository = grupoEstudioRepository;
        this.usuarioRepository = usuarioRepository;
        this.mensajeChatMapper = mensajeChatMapper;
    }

    @Transactional
    public MensajeChatResponse enviarMensaje(Long idGrupo, EnviarMensajeRequest request) {

        GrupoEstudio grupo = buscarGrupo(idGrupo);

        Usuario autor = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        validarPertenencia(idGrupo, autor.getIdUsuario());

        MensajeChat mensaje = new MensajeChat();
        mensaje.setGrupo(grupo);
        mensaje.setAutor(autor);
        mensaje.setContenido(request.getContenido().trim());

        MensajeChat mensajeGuardado = mensajeChatRepository.save(mensaje);

        return mensajeChatMapper.toMensajeResponse(mensajeGuardado);
    }

    @Transactional
    public List<MensajeChatResponse> listarMensajes(Long idGrupo, Long idUsuario) {

        buscarGrupo(idGrupo);
        validarPertenencia(idGrupo, idUsuario);

        return mensajeChatRepository.findByGrupo_IdGrupoOrderByFechaEnvioAsc(idGrupo)
                .stream()
                .map(mensajeChatMapper::toMensajeResponse)
                .toList();
    }

    @Transactional
    public String eliminarMensaje(Long idGrupo, Long idMensaje, Long idUsuario) {

        MensajeChat mensaje = mensajeChatRepository
                .findByIdMensajeAndGrupo_IdGrupo(idMensaje, idGrupo)
                .orElseThrow(() -> new MensajeNoEncontradoException("El mensaje no existe"));

        if (!mensaje.getAutor().getIdUsuario().equals(idUsuario)) {
            throw new MensajeAjenoException("Solo puedes eliminar tus propios mensajes");
        }

        long minutosTranscurridos = Duration
                .between(mensaje.getFechaEnvio(), LocalDateTime.now())
                .toMinutes();

        if (minutosTranscurridos >= MINUTOS_PARA_ELIMINAR) {
            throw new TiempoEliminacionExpiradoException(
                    "Solo puedes eliminar un mensaje dentro de los 2 minutos posteriores a su envío"
            );
        }

        mensajeChatRepository.delete(mensaje);

        return "Se eliminó correctamente";
    }

    private GrupoEstudio buscarGrupo(Long idGrupo) {
        return grupoEstudioRepository.findById(idGrupo)
                .orElseThrow(() -> new GrupoNoEncontradoException("El grupo de estudio no existe"));
    }

    private void validarPertenencia(Long idGrupo, Long idUsuario) {
        if (!mensajeChatRepository.usuarioPerteneceAlGrupo(idGrupo, idUsuario)) {
            throw new UsuarioNoPerteneceAlGrupoException("No perteneces a este grupo");
        }
    }
}