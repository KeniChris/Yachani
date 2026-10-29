package com.yachaniapi.notificacion.controller;

import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> listarNotificaciones(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(notificacionService.listarNotificaciones(idUsuario));
    }

    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<NotificacionResponse> marcarComoLeida(
            @PathVariable Long idNotificacion,
            @RequestParam Long idUsuario) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(idNotificacion, idUsuario));
    }
}
