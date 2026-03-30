-- =============================================================================
-- V2: Correcciones de integridad referencial en entidades JPA
-- =============================================================================

-- Fix 1: Eliminar columna legacy 'id' si existe en mantenimientos
-- (la entidad MantenimientoJpa generaba esta columna adicional a id_mantenimiento)
-- NOTA: Ejecutar solo si la columna 'id' existe y NO es la PK real.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'mantenimientos' AND column_name = 'id'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'mantenimientos' AND column_name = 'id_mantenimiento'
    ) THEN
        ALTER TABLE mantenimientos DROP COLUMN IF EXISTS id CASCADE;
        RAISE NOTICE 'Columna legacy "id" eliminada de mantenimientos';
    END IF;
END $$;

-- Fix 3: FK de mantenimientos.fk_programado -> mantenimientos_programados.id_programado
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_mantenimiento_programado'
    ) THEN
        ALTER TABLE mantenimientos
            ADD CONSTRAINT fk_mantenimiento_programado
            FOREIGN KEY (fk_programado)
            REFERENCES mantenimientos_programados (id_programado);
        RAISE NOTICE 'FK fk_mantenimiento_programado creada';
    END IF;
END $$;

-- Fix 5: Nueva columna FK para usuario de actualizacion
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'actualizacion_activos' AND column_name = 'fk_usuario_actualizacion'
    ) THEN
        ALTER TABLE actualizacion_activos
            ADD COLUMN fk_usuario_actualizacion INTEGER;
        ALTER TABLE actualizacion_activos
            ADD CONSTRAINT fk_actualizacion_usuario
            FOREIGN KEY (fk_usuario_actualizacion)
            REFERENCES usuarios (id_usuario);
        RAISE NOTICE 'FK fk_actualizacion_usuario creada en actualizacion_activos';
    END IF;
END $$;

-- Migrar datos existentes: resolver usuario_actualizacion (varchar) -> fk_usuario_actualizacion (int)
UPDATE actualizacion_activos aa
SET fk_usuario_actualizacion = u.id_usuario
FROM usuarios u
WHERE aa.usuario_actualizacion = u.correo
  AND aa.fk_usuario_actualizacion IS NULL;

-- Fix 7: FK de equipos.fk_ubicacion -> ubicaciones.id_ubicacion
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_equipo_ubicacion'
    ) THEN
        -- Hibernate ddl-auto=update crea la columna pero no siempre la FK
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'equipos' AND column_name = 'fk_ubicacion'
        ) THEN
            ALTER TABLE equipos ADD COLUMN fk_ubicacion INTEGER;
        END IF;
        ALTER TABLE equipos
            ADD CONSTRAINT fk_equipo_ubicacion
            FOREIGN KEY (fk_ubicacion)
            REFERENCES ubicaciones (id_ubicacion);
        RAISE NOTICE 'FK fk_equipo_ubicacion creada en equipos';
    END IF;
END $$;
