package com.yachaniapi.usuario.entity;

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
}