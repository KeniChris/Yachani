package com.yachaniapi.sesion.scheduler;

import com.yachaniapi.sesion.entity.SesionEstudio;
import com.yachaniapi.sesion.repository.SesionEstudioRepository;
import com.yachaniapi.sesion.service.NotificacionSesionService;
import com.yachaniapi.sesion.service.SesionEstudioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecordatorioSesionScheduler {

    private final SesionEstudioRepository sesionRepository;
    private final NotificacionSesionService notificacionService;
    private final long minutosAntes;

    public RecordatorioSesionScheduler(
            SesionEstudioRepository sesionRepository,
            NotificacionSesionService notificacionService,
            @Value("${yachani.recordatorio.minutos-antes:60}") long minutosAntes) {

        this.sesionRepository = sesionRepository;
        this.notificacionService = notificacionService;
        this.minutosAntes = minutosAntes;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void enviarRecordatorios() {

        LocalDateTime ahora = LocalDateTime.now();

        List<SesionEstudio> sesionesProximas = sesionRepository
                .findByEstadoAndRecordatorioEnviadoFalseAndFechaHoraInicioBetween(
                        SesionEstudioService.ESTADO_PROGRAMADA,
                        ahora,
                        ahora.plusMinutes(minutosAntes)
                );

        for (SesionEstudio sesion : sesionesProximas) {
            notificacionService.enviarRecordatorio(sesion);
            sesion.setRecordatorioEnviado(true);
            sesionRepository.save(sesion);
        }
    }
}