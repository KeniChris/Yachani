package com.yachaniapi.contacto.service;

import com.yachaniapi.contacto.dto.ContactoRequest;
import com.yachaniapi.usuario.service.CorreoService;
import org.springframework.stereotype.Service;

@Service
public class ContactoService {
    private final CorreoService correoService;
    private final LimiteContacto limite;

    public ContactoService(CorreoService correoService, LimiteContacto limite) {
        this.correoService = correoService;
        this.limite = limite;
    }

    public void enviar(ContactoRequest request, String direccion) {
        limite.verificar(direccion);
        correoService.enviarContacto(
                request.nombre(), request.correo(), request.asunto(), request.mensaje());
    }
}
