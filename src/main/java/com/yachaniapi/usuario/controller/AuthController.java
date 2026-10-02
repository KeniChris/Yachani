package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.LoginRequest;
import com.yachaniapi.usuario.dto.RegistroRequest;
import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}