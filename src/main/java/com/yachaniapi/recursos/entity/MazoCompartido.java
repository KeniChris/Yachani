package com.yachaniapi.recursos.entity;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "mazos_compartidos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_mazo_grupo",
                columnNames = {"id_mazo", "id_grupo"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class MazoCompartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compartido")
    private Long idCompartido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mazo", nullable = false)
    private MazoFlashcards mazo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grupo", nullable = false)
    private GrupoEstudio grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    private TemaGrupo tema;

    @Column(
            name = "fecha_compartido",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaCompartido;

    @PrePersist
    public void inicializar() {
        if (fechaCompartido == null) {
            fechaCompartido = LocalDateTime.now();
        }
    }
}