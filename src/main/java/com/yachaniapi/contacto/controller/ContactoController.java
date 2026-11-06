package com.yachaniapi.contacto.controller;

import com.yachaniapi.contacto.dto.ContactoRequest;
import com.yachaniapi.contacto.service.ContactoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/contacto")
public class ContactoController {
    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> enviar(
            @Valid @RequestBody ContactoRequest request, HttpServletRequest solicitud) {
        contactoService.enviar(request, solicitud.getRemoteAddr());
        return ResponseEntity.ok(Map.of("mensaje",
                "Tu mensaje fue enviado. Te responderemos al correo que indicaste."));
    }
}
