package com.yachaniapi.usuario.controller;

import com.yachaniapi.usuario.dto.LoginRequest;
import com.yachaniapi.usuario.dto.RegistroRequest;
import com.yachaniapi.usuario.dto.UsuarioResponse;
import com.yachaniapi.usuario.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yachaniapi.usuario.dto.RecuperarContrasenaRequest;
import com.yachaniapi.usuario.dto.RestablecerContrasenaRequest;
import com.yachaniapi.usuario.dto.VerificarCodigoRequest;
import com.yachaniapi.usuario.service.RecuperacionContrasenaService;
import jakarta.validation.Valid;

import java.util.Map;

/**
 * Recibe las solicitudes de registro e inicio de sesión.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RecuperacionContrasenaService recuperacionService;

    public AuthController(
            AuthService authService,
            RecuperacionContrasenaService recuperacionService) {

        this.authService = authService;
        this.recuperacionService = recuperacionService;
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
     * Envía un código para recuperar la contraseña.
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Map<String, String>> solicitarCodigo(
            @Valid @RequestBody RecuperarContrasenaRequest request) {

        recuperacionService.solicitarCodigo(request);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Si el correo está registrado, recibirás un código de recuperación"
                )
        );
    }

    /**
     * Comprueba el código enviado al correo.
     */
    @PostMapping("/verificar-codigo")
    public ResponseEntity<Map<String, String>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request) {

        recuperacionService.verificarCodigo(request);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Código verificado correctamente"
                )
        );
    }

    /**
     * Guarda la nueva contraseña.
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Map<String, String>> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaRequest request) {

        recuperacionService.restablecerContrasena(request);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Contraseña restablecida correctamente"
                )
        );
    }
}