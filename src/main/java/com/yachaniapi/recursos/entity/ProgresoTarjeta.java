package com.yachaniapi.recursos.entity;

import com.yachaniapi.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "progresos_tarjeta",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_usuario", "id_tarjeta"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class ProgresoTarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_progreso")
    private Long idProgreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tarjeta", nullable = false)
    private TarjetaFlashcard tarjeta;

    @Column(nullable = false)
    private boolean vista = false;

    @Column(nullable = false)
    private boolean aprendida = false;

    @Column(name = "fecha_ultima_practica", nullable = false)
    private LocalDateTime fechaUltimaPractica;

    @PrePersist
    @PreUpdate
    public void actualizarFecha() {
        fechaUltimaPractica = LocalDateTime.now();
    }
}