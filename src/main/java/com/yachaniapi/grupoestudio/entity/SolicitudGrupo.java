package com.yachaniapi.grupoestudio.entity;

import com.yachaniapi.usuario.entity.Estudiante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "solicitudes_grupo",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_grupo_estudiante",
                columnNames = {"id_grupo", "id_estudiante"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SolicitudGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grupo", nullable = false)
    private GrupoEstudio grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false, length = 20)
    private String estado;
}
