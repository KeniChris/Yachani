package com.yachaniapi.sesion.entity;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_estudio")
@Getter
@Setter
@NoArgsConstructor
public class SesionEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idSesion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grupo", nullable = false)
    private GrupoEstudio grupo;

    @Column(nullable = false, length = 150)
    private String tema;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(nullable = false, length = 200)
    private String lugar;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "recordatorio_enviado", nullable = false)
    private Boolean recordatorioEnviado = false;
}