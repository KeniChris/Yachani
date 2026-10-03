package com.yachaniapi.sesion.controller;

import com.yachaniapi.sesion.dto.NotificacionResponse;
import com.yachaniapi.sesion.service.NotificacionSesionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estudiantes/{idEstudiante}/notificaciones")
public class NotificacionSesionController {

    private final NotificacionSesionService notificacionService;

    public NotificacionSesionController(NotificacionSesionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> listarNotificaciones(
            @PathVariable Long idEstudiante) {

        return ResponseEntity.ok(notificacionService.listarNotificaciones(idEstudiante));
    }
}