package com.yachaniapi.recursos.entity;

import com.yachaniapi.grupoestudio.entity.GrupoEstudio;
import com.yachaniapi.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "materiales_estudio")
@Getter
@Setter
@NoArgsConstructor
public class MaterialEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private Long idMaterial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_grupo",
            nullable = false,
            updatable = false
    )
    private GrupoEstudio grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private TemaGrupo tema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_autor",
            nullable = false,
            updatable = false
    )
    private Usuario autor;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(
            name = "tipo_archivo",
            nullable = false,
            length = 150
    )
    private String tipoArchivo;

    @Column(name = "tamano_archivo", nullable = false)
    private Long tamanoArchivo;

    @Column(
            name = "id_archivo_nube",
            nullable = false,
            length = 500
    )
    private String idArchivoNube;

    @Column(name = "tipo_recurso_nube", length = 30)
    private String tipoRecursoNube;

    @Column(name = "tipo_acceso_nube", length = 30)
    private String tipoAccesoNube;

    @Column(
            name = "fecha_publicacion",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaPublicacion;

    @PrePersist
    public void asignarFechaPublicacion() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
    }
}