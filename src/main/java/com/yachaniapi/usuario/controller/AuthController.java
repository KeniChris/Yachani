package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.LoginRequest;
import com.yachaniapi.usuario.dto.RegistroRequest;
import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Recibe las solicitudes de registro e inicio de sesión.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un estudiante o tutor.
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @RequestBody RegistroRequest request) {

        UsuarioResponse respuesta = authService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    /**
     * Valida las credenciales para iniciar sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(
            @RequestBody LoginRequest request) {

        UsuarioResponse respuesta = authService.iniciarSesion(request);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Devuelve un error 400 cuando los datos no son válidos.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarError(
            IllegalArgumentException exception) {

        return ResponseEntity
                .badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }
}