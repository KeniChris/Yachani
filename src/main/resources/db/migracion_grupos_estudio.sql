-- Ejecutar UNA VEZ sobre una base de datos existente, ANTES de levantar la aplicación.
-- Permite que estudiantes creen grupos y agrega el tipo de grupo.

-- 1. Renombrar la columna del creador y apuntarla a usuarios (antes apuntaba a tutores)
DO $$
DECLARE fk RECORD;
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'grupos_estudio' AND column_name = 'id_tutor_creador') THEN
        FOR fk IN
            SELECT tc.constraint_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
            WHERE tc.table_name = 'grupos_estudio'
              AND tc.constraint_type = 'FOREIGN KEY'
              AND kcu.column_name = 'id_tutor_creador'
        LOOP
            EXECUTE 'ALTER TABLE grupos_estudio DROP CONSTRAINT ' || quote_ident(fk.constraint_name);
        END LOOP;

        ALTER TABLE grupos_estudio RENAME COLUMN id_tutor_creador TO id_creador;
        ALTER TABLE grupos_estudio
            ADD CONSTRAINT fk_grupos_estudio_creador
            FOREIGN KEY (id_creador) REFERENCES usuarios (id_usuario);
    END IF;
END $$;

-- 2. Tipo de grupo: los grupos existentes fueron creados por tutores
ALTER TABLE grupos_estudio ADD COLUMN IF NOT EXISTS tipo_grupo VARCHAR(20);
UPDATE grupos_estudio SET tipo_grupo = 'TUTOR' WHERE tipo_grupo IS NULL;
ALTER TABLE grupos_estudio ALTER COLUMN tipo_grupo SET NOT NULL;

-- 3. Unificar el estado con el resto del sistema (mayúsculas)
UPDATE grupos_estudio SET estado = 'ACTIVO' WHERE estado = 'Activo';
