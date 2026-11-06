package com.yachaniapi.contacto.service;

import com.yachaniapi.contacto.exception.LimiteContactoException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** Límite local por dirección de conexión: no se confía en cabeceras enviadas por el visitante. */
@Service
public class LimiteContacto {
    private static final int MAX_INTENTOS = 3;
    private static final int MAX_DIRECCIONES = 10_000;
    private static final Duration VENTANA = Duration.ofMinutes(15);
    private final Map<String, Ventana> direcciones = new HashMap<>();
    private final Clock reloj;

    public LimiteContacto() {
        this(Clock.systemUTC());
    }

    LimiteContacto(Clock reloj) {
        this.reloj = reloj;
    }

    public synchronized void verificar(String direccion) {
        Instant ahora = reloj.instant();
        direcciones.values().removeIf(ventana -> !ventana.fin().isAfter(ahora));
        Ventana ventana = direcciones.get(direccion);
        if (ventana == null) {
            if (direcciones.size() >= MAX_DIRECCIONES) {
                throw new LimiteContactoException(VENTANA.toSeconds());
            }
            direcciones.put(direccion, new Ventana(ahora.plus(VENTANA), 1));
        } else if (ventana.intentos() >= MAX_INTENTOS) {
            throw new LimiteContactoException(
                    Math.max(1, Duration.between(ahora, ventana.fin()).toSeconds() + 1));
        } else {
            direcciones.put(direccion, new Ventana(ventana.fin(), ventana.intentos() + 1));
        }
    }

    private record Ventana(Instant fin, int intentos) {}
}
