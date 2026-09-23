package com.yachaniapi.exception;

import com.yachaniapi.usuario.exception.CorreoDuplicadoException;
import com.yachaniapi.usuario.exception.CredencialesIncorrectasException;
import com.yachaniapi.usuario.exception.UsuarioNoEncontradoException;
import com.yachaniapi.usuario.exception.UsuarioNoEsEstudianteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Devuelve un error cuando no se encuentra al usuario
     */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEncontrado(
            UsuarioNoEncontradoException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    /**
     * Devuelve un error cuando el correo ya está registrado.
     */
    @ExceptionHandler(CorreoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarCorreoDuplicado(
            CorreoDuplicadoException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    /**
     * Devuelve un error cuando el correo o la contraseña son incorrectos
     */
    @ExceptionHandler(CredencialesIncorrectasException.class)
    public ResponseEntity<Map<String, String>> manejarCredencialesIncorrectas(
            CredencialesIncorrectasException exception) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    /**
     * Devuelve un error cuando la operación requiere un estudiante
     */
    @ExceptionHandler(UsuarioNoEsEstudianteException.class)
    public ResponseEntity<Map<String, String>> manejarUsuarioNoEsEstudiante(
            UsuarioNoEsEstudianteException exception) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    /**
     * Devuelve el mensaje de una validación incorrecta
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException exception) {

        String mensaje = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Los datos enviados no son válidos");

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", mensaje));
    }

    /**
     * Maneja errores de validación realizados dentro de los services
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }
}