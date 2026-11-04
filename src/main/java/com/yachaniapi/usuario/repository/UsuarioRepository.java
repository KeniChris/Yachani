package com.yachaniapi.usuario.repository;

import com.yachaniapi.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repositorio encargado del acceso a los datos de los usuarios.
 * Spring Data JPA genera automáticamente las consultas principales.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca un usuario por su correo electrónico.
     * No diferencia entre letras mayúsculas y minúsculas.
     */
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    /**
     * Comprueba si ya existe un usuario registrado con el correo indicado.
     * Se utilizará para evitar registros duplicados.
     */
    boolean existsByCorreoIgnoreCase(String correo);

    @org.springframework.data.jpa.repository.Query(value =
            "select id_usuario from usuarios where id_usuario = :id for update", nativeQuery = true)
    Optional<Long> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);

}