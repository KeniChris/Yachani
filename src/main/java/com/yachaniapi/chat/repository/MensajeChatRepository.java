package com.yachaniapi.chat.repository;

import com.yachaniapi.chat.entity.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {

    List<MensajeChat> findByGrupo_IdGrupoOrderByFechaEnvioAsc(Long idGrupo);

    Optional<MensajeChat> findByIdMensajeAndGrupo_IdGrupo(Long idMensaje, Long idGrupo);

    @Query("SELECT COUNT(g) > 0 FROM GrupoEstudio g LEFT JOIN g.participantes p " +
           "WHERE g.idGrupo = :idGrupo " +
           "AND (g.creador.idUsuario = :idUsuario OR p.idUsuario = :idUsuario)")
    boolean usuarioPerteneceAlGrupo(@Param("idGrupo") Long idGrupo,
                                    @Param("idUsuario") Long idUsuario);
}