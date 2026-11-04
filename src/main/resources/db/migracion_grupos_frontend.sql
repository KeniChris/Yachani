-- PostgreSQL. Detén el backend antes de ejecutar este script.
-- No se ejecuta automáticamente: úsalo en el SQL Editor de tu base de desarrollo.
BEGIN;

ALTER TABLE grupos_estudio
    ADD COLUMN IF NOT EXISTS permitir_publicaciones BOOLEAN;

UPDATE grupos_estudio
SET permitir_publicaciones = TRUE
WHERE permitir_publicaciones IS NULL OR tipo_grupo = 'ESTUDIANTES';

ALTER TABLE grupos_estudio
    ALTER COLUMN permitir_publicaciones SET DEFAULT TRUE,
    ALTER COLUMN permitir_publicaciones SET NOT NULL;

ALTER TABLE solicitudes_grupo
    ADD COLUMN IF NOT EXISTS fecha_solicitud TIMESTAMP;

-- Para registros antiguos sin fecha se usa la fecha de esta migración.
UPDATE solicitudes_grupo
SET fecha_solicitud = CURRENT_TIMESTAMP
WHERE fecha_solicitud IS NULL;

ALTER TABLE solicitudes_grupo
    ALTER COLUMN fecha_solicitud SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN fecha_solicitud SET NOT NULL;

-- Se detiene si existen duplicados; no se borra ninguna membresía.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM grupo_miembros
        GROUP BY id_grupo, id_estudiante
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Hay membresías duplicadas. Revísalas antes de continuar.';
    END IF;
END $$;

-- El creador estudiante también ocupa un cupo y pertenece a la lista.
INSERT INTO grupo_miembros (id_grupo, id_estudiante)
SELECT g.id_grupo, g.id_creador
FROM grupos_estudio g
JOIN estudiantes e ON e.id_usuario = g.id_creador
WHERE g.tipo_grupo = 'ESTUDIANTES'
  AND NOT EXISTS (
      SELECT 1 FROM grupo_miembros m
      WHERE m.id_grupo = g.id_grupo AND m.id_estudiante = g.id_creador
  );

-- Si incorporar al creador supera la capacidad antigua, se amplía lo necesario.
-- Se conservan todos los integrantes.
UPDATE grupos_estudio g
SET capacidad_maxima = GREATEST(
    g.capacidad_maxima,
    (SELECT COUNT(*)::INTEGER FROM grupo_miembros m WHERE m.id_grupo = g.id_grupo)
)
WHERE g.tipo_grupo = 'ESTUDIANTES';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_grupo_miembro'
          AND conrelid = 'grupo_miembros'::regclass
    ) THEN
        ALTER TABLE grupo_miembros
            ADD CONSTRAINT uk_grupo_miembro UNIQUE (id_grupo, id_estudiante);
    END IF;
END $$;

COMMIT;
