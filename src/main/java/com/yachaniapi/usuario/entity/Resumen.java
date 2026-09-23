package com.yachaniapi.usuario.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumenes")
@Getter
@Setter
public class Resumen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResumen;

    @Column(nullable = false, length = 150)
    private String titulo;


    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String tipoArchivo;

    /**
     * Ubicación donde se guardó el archivo dentro del servidor
     */
    @Column(nullable = false)
    private String rutaArchivo;

    /**
     * Tamaño del archivo en bytes
     */
    @Column(nullable = false)
    private Long tamanoArchivo;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    /**
     * Estudiante que publicó el resumen en su perfil.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    /**
     * fecha asignada automaticamente
     */
    @PrePersist
    public void asignarFechaPublicacion() {
        fechaPublicacion = LocalDateTime.now();
    }
}