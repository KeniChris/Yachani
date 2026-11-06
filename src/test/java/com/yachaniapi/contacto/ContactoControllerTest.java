package com.yachaniapi.contacto;

import com.yachaniapi.config.CorsConfig;
import com.yachaniapi.config.SecurityConfig;
import com.yachaniapi.contacto.controller.ContactoController;
import com.yachaniapi.contacto.exception.LimiteContactoException;
import com.yachaniapi.contacto.service.ContactoService;
import com.yachaniapi.exception.GlobalExceptionHandler;
import com.yachaniapi.usuario.exception.EnvioCorreoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(ContactoControllerTest.Config.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:4200")
class ContactoControllerTest {
    private static final String DATOS = """
            {"nombre":" Ana Pérez ","correo":"ana@example.com","asunto":"Pregunta","mensaje":"Necesito ayuda con un grupo."}
            """;
    @Autowired WebApplicationContext contexto;
    @Autowired ContactoService service;
    MockMvc mvc;

    @BeforeEach void preparar() {
        reset(service);
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    @Test void enviaSinIniciarSesionYNormalizaLosDatos() throws Exception {
        mvc.perform(post("/contacto").contentType("application/json").content(DATOS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value(
                        "Tu mensaje fue enviado. Te responderemos al correo que indicaste."));
        verify(service).enviar(argThat(request -> request.nombre().equals("Ana Pérez")
                && request.correo().equals("ana@example.com")), eq("127.0.0.1"));
    }

    @Test void rechazaCamposVaciosCorreoInvalidoYLongitudesExcesivas() throws Exception {
        for (String datos : new String[] {
                DATOS.replace(" Ana Pérez ", "   "),
                DATOS.replace("ana@example.com", "correo-invalido"),
                DATOS.replace("Pregunta", "a".repeat(151)),
                DATOS.replace("Necesito ayuda con un grupo.", "a".repeat(5001)) }) {
            mvc.perform(post("/contacto").contentType("application/json").content(datos))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").isString());
        }
        verifyNoInteractions(service);
    }

    @Test void rechazaSaltosDeLineaEnCabecerasDelCorreo() throws Exception {
        mvc.perform(post("/contacto").contentType("application/json")
                        .content(DATOS.replace("Pregunta", "Pregunta\\r\\nBcc: otra@example.com")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void unFalloSmtpNoDevuelveConfirmacionDeEnvio() throws Exception {
        doThrow(new EnvioCorreoException("No fue posible enviar tu mensaje"))
                .when(service).enviar(any(), anyString());
        mvc.perform(post("/contacto").contentType("application/json").content(DATOS))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje").value("No fue posible enviar tu mensaje"));
    }

    @Test void excesoDeMensajesDevuelve429YTiempoDeEspera() throws Exception {
        doThrow(new LimiteContactoException(120)).when(service).enviar(any(), anyString());
        mvc.perform(post("/contacto").contentType("application/json").content(DATOS))
                .andExpect(status().isTooManyRequests()).andExpect(header().string("Retry-After", "120"))
                .andExpect(jsonPath("$.codigo").value("LIMITE_CONTACTO"));
    }

    @Test void conservaElCorsParaElFormularioPublico() throws Exception {
        mvc.perform(options("/contacto").header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test void noAbreLasRutasPrivadasNiOtrosMetodos() throws Exception {
        mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/contacto")).andExpect(status().isUnauthorized());
        mvc.perform(post("/grupos").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Configuration
    @EnableWebMvc
    @Import({ContactoController.class, GlobalExceptionHandler.class, SecurityConfig.class, CorsConfig.class})
    static class Config {
        @Bean ContactoService contactoService() { return mock(ContactoService.class); }
        @Bean JwtDecoder jwtDecoder() { return mock(JwtDecoder.class); }
    }
}
