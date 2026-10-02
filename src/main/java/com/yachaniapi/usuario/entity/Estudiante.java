package com.yachaniapi.usuario.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa al usuario que utiliza la plataforma como estudiante.
 * Hereda los datos personales y las credenciales de Usuario.
 */
@Entity
@Table(name = "estudiantes")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
public class Estudiante extends Usuario {

    @Column(length = 120)
    private String universidad;

    @Column(length = 120)
    private String carrera;

    @Column(name = "modalidad_preferida", length = 50)
    private String modalidadPreferida;

    @Column(name = "metodo_preferido", length = 50)
    private String metodoPreferido;

    /**
     * Lista de temas que le interesan al estudiante.
     * Se almacena en una tabla relacionada.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "estudiante_temas_interes",
            joinColumns = @JoinColumn(name = "id_usuario")
    )
    @Column(name = "tema_interes", nullable = false, length = 100)
    private List<String> temasInteres = new ArrayList<>();
}