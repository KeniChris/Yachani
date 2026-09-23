package com.yachaniapi.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un curso registrado por un estudiante
 * junto con su nivel de conocimiento.
 */
@Entity
@Table(
        name = "conocimientos_academicos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_estudiante_curso",
                columnNames = {"id_estudiante", "curso"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class ConocimientoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conocimiento")
    private Long idConocimiento;

    /**
     * Estudiante propietario de este conocimiento académico.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false, length = 120)
    private String curso;

    @Column(nullable = false, length = 30)
    private String nivel;
}