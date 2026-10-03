package com.yachaniapi.chat.controller;

import com.yachaniapi.chat.dto.EnviarMensajeRequest;
import com.yachaniapi.chat.dto.MensajeChatResponse;
import com.yachaniapi.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/grupos/{idGrupo}/mensajes")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<MensajeChatResponse> enviarMensaje(
            @PathVariable Long idGrupo,
            @Valid @RequestBody EnviarMensajeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chatService.enviarMensaje(idGrupo, request));
    }

    @GetMapping
    public ResponseEntity<List<MensajeChatResponse>> listarMensajes(
            @PathVariable Long idGrupo,
            @RequestParam Long idUsuario) {

        return ResponseEntity.ok(chatService.listarMensajes(idGrupo, idUsuario));
    }

    @DeleteMapping("/{idMensaje}")
    public ResponseEntity<Map<String, String>> eliminarMensaje(
            @PathVariable Long idGrupo,
            @PathVariable Long idMensaje,
            @RequestParam Long idUsuario) {

        String mensaje = chatService.eliminarMensaje(idGrupo, idMensaje, idUsuario);

        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}