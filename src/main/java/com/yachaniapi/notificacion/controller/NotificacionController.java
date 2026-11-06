package com.yachaniapi.notificacion.controller;

import com.yachaniapi.notificacion.dto.NotificacionResponse;
import com.yachaniapi.notificacion.service.NotificacionService;
import com.yachaniapi.grupoestudio.dto.response.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<PaginaResponse<NotificacionResponse>> listar(
            @RequestParam(required = false) Long idUsuario,
            @PageableDefault(size = 20, sort = {"fechaCreacion", "idNotificacion"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificacionService.listar(idUsuario, pageable));
    }

    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<NotificacionResponse> marcarComoLeida(
            @PathVariable Long idNotificacion, @RequestParam(required = false) Long idUsuario) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(idNotificacion, idUsuario));
    }

    @PatchMapping("/leidas")
    public ResponseEntity<Map<String, Integer>> marcarTodas() {
        return ResponseEntity.ok(Map.of("actualizadas", notificacionService.marcarTodasLeidas()));
    }

    @GetMapping("/no-leidas/conteo")
    public ResponseEntity<Map<String, Long>> contarNoLeidas() {
        return ResponseEntity.ok(Map.of("cantidad", notificacionService.contarNoLeidas()));
    }
}
