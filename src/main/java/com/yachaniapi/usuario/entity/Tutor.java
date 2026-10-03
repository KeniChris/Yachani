package com.yachaniapi.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutores")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
public class Tutor extends Usuario {
    /**
     * Descripción que el tutor muestra en su perfil
     */
    @Column(length = 500)
    private String presentacion;

    /**
     * Metodo que utiliza el tutor para enseñar
     */
    @Column(name = "metodo_ensenanza")
    private String metodoEnsenanza;
}