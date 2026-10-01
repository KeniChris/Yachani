package com.yachaniapi.recursos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tarjetas_flashcard")
@Getter
@Setter
@NoArgsConstructor
public class TarjetaFlashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Long idTarjeta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mazo", nullable = false)
    private MazoFlashcards mazo;

    @Column(nullable = false, length = 1000)
    private String pregunta;

    @Column(nullable = false, length = 1000)
    private String respuesta;

    @Column(name = "orden_tarjeta", nullable = false)
    private Integer orden;

    // Al eliminarla de la vista, conservamos su referencia para el progreso.
    @Column(nullable = false)
    private boolean activa = true;
}