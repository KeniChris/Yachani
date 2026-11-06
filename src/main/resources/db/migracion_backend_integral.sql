-- PostgreSQL. Ejecutar después del bloque Grupos y antes de arrancar la nueva API.
BEGIN;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM usuarios
        GROUP BY LOWER(BTRIM(correo)) HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Hay correos duplicados sin distinguir mayúsculas. Revísalos antes de continuar.';
    END IF;
    IF EXISTS (
        SELECT 1 FROM conocimientos_academicos
        GROUP BY id_estudiante, LOWER(BTRIM(curso)) HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Hay cursos duplicados por estudiante. Revísalos antes de continuar.';
    END IF;
END $$;

UPDATE usuarios SET correo = LOWER(BTRIM(correo));
CREATE UNIQUE INDEX IF NOT EXISTS uk_usuarios_correo_normalizado
    ON usuarios (LOWER(BTRIM(correo)));
CREATE UNIQUE INDEX IF NOT EXISTS uk_conocimiento_curso_normalizado
    ON conocimientos_academicos (id_estudiante, LOWER(BTRIM(curso)));

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS configuracion_completada BOOLEAN;
UPDATE usuarios SET configuracion_completada = FALSE WHERE configuracion_completada IS NULL;
ALTER TABLE usuarios
    ALTER COLUMN configuracion_completada SET DEFAULT FALSE,
    ALTER COLUMN configuracion_completada SET NOT NULL;

ALTER TABLE tutores ADD COLUMN IF NOT EXISTS universidad VARCHAR(120);
ALTER TABLE tutores ADD COLUMN IF NOT EXISTS carrera VARCHAR(120);

-- Recalcular el estado según los datos que realmente pide el formulario actual.
UPDATE usuarios u SET configuracion_completada =
    CASE
      WHEN EXISTS (SELECT 1 FROM estudiantes e WHERE e.id_usuario = u.id_usuario) THEN
        EXISTS (
            SELECT 1 FROM estudiantes e
            WHERE e.id_usuario = u.id_usuario
              AND NULLIF(BTRIM(e.universidad), '') IS NOT NULL
              AND NULLIF(BTRIM(e.carrera), '') IS NOT NULL
              AND NULLIF(BTRIM(e.modalidad_preferida), '') IS NOT NULL
              AND NULLIF(BTRIM(e.metodo_preferido), '') IS NOT NULL
        )
        AND EXISTS (
            SELECT 1 FROM conocimientos_academicos c WHERE c.id_estudiante = u.id_usuario
        )
        AND NOT EXISTS (
            SELECT 1 FROM conocimientos_academicos c WHERE c.id_estudiante = u.id_usuario
            AND (NULLIF(BTRIM(c.curso), '') IS NULL OR NULLIF(BTRIM(c.nivel), '') IS NULL)
        )
      WHEN EXISTS (SELECT 1 FROM tutores t WHERE t.id_usuario = u.id_usuario) THEN
        EXISTS (
            SELECT 1 FROM tutores t WHERE t.id_usuario = u.id_usuario
              AND NULLIF(BTRIM(t.presentacion), '') IS NOT NULL
              AND NULLIF(BTRIM(t.metodo_ensenanza), '') IS NOT NULL
        )
      ELSE FALSE
    END;

-- La bandeja general incorpora las sesiones sin perder los datos antiguos.
ALTER TABLE notificaciones ALTER COLUMN mensaje TYPE VARCHAR(500);
ALTER TABLE notificaciones ADD COLUMN IF NOT EXISTS id_sesion BIGINT;
ALTER TABLE notificaciones ADD COLUMN IF NOT EXISTS id_notificacion_sesion_origen BIGINT;
CREATE UNIQUE INDEX IF NOT EXISTS uk_notificacion_sesion_origen
    ON notificaciones (id_notificacion_sesion_origen);

UPDATE notificaciones n
SET id_grupo = s.id_grupo
FROM solicitudes_grupo s
WHERE n.id_solicitud = s.id_solicitud AND n.id_grupo IS NULL;

ALTER TABLE notificaciones_sesion
    ADD COLUMN IF NOT EXISTS migrada BOOLEAN NOT NULL DEFAULT FALSE;

INSERT INTO notificaciones (
    id_destinatario, id_solicitud, id_grupo, id_sesion, tipo, mensaje,
    leida, fecha_creacion, id_notificacion_sesion_origen
)
SELECT ns.id_estudiante, NULL, s.id_grupo, s.id_sesion,
       CASE WHEN ns.tipo = 'RECORDATORIO' THEN 'SESION_RECORDATORIO'
            ELSE 'SESION_MODIFICADA' END,
       CASE WHEN ns.tipo = 'RECORDATORIO'
            THEN 'Recordatorio de la sesión ' || s.tema
            ELSE 'La sesión ' || s.tema || ' fue modificada' END,
       COALESCE(ns.leida, FALSE), ns.fecha_creacion, ns.id_notificacion
FROM notificaciones_sesion ns
JOIN sesiones_estudio s ON s.id_sesion = ns.id_sesion
WHERE ns.migrada = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM notificaciones n
      WHERE n.id_notificacion_sesion_origen = ns.id_notificacion
  );

UPDATE notificaciones_sesion ns SET migrada = TRUE
WHERE EXISTS (
    SELECT 1 FROM notificaciones n
    WHERE n.id_notificacion_sesion_origen = ns.id_notificacion
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_notificacion_sesion'
          AND conrelid = 'notificaciones'::regclass
    ) THEN
        ALTER TABLE notificaciones ADD CONSTRAINT fk_notificacion_sesion
            FOREIGN KEY (id_sesion) REFERENCES sesiones_estudio(id_sesion);
    END IF;
END $$;

COMMIT;
