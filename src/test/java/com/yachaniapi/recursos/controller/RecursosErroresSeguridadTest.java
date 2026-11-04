package com.yachaniapi.recursos.controller;

import com.yachaniapi.recursos.exception.RecursosExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecursosErroresSeguridadTest {
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new Sonda())
                .setControllerAdvice(new RecursosExceptionHandler()).build();
    }

    @Test
    void unaIdentidadInvalidaProduce401EnLugarDelErrorGenerico500() throws Exception {
        mvc.perform(get("/sonda/identidad")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Inicia sesión con un token válido"));
    }

    @Test
    void unPermisoDenegadoProduce403EnLugarDelErrorGenerico500() throws Exception {
        mvc.perform(get("/sonda/permiso")).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("No tienes permiso para esta operación"));
    }

    @RestController
    static class Sonda {
        @GetMapping("/sonda/identidad")
        String identidad() { throw new BadCredentialsException("Identidad inválida"); }

        @GetMapping("/sonda/permiso")
        String permiso() { throw new AccessDeniedException("Permiso denegado"); }
    }
}
