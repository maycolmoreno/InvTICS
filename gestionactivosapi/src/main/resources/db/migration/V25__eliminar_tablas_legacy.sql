-- V25: Eliminar tablas legacy y columnas obsoletas
-- Cubre campos de EquiposJpa no incluidos en V18-V23, migra datos de activos,
-- y elimina activos / actualizacion_activos del esquema.

-- 1. Columnas de equipos que JPA espera y que ninguna migración previa añadió
ALTER TABLE equipos ADD COLUMN IF NOT EXISTS fecha_adquisicion DATE;
ALTER TABLE equipos ADD COLUMN IF NOT EXISTS valor_actual     DOUBLE PRECISION;
ALTER TABLE equipos ADD COLUMN IF NOT EXISTS descripcion      TEXT;

-- 2. Migrar datos históricos activos → equipos (idempotente)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'activos'
    )
    AND EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'equipos' AND column_name = 'activo_id'
    ) THEN
        UPDATE equipos e
        SET fecha_adquisicion = COALESCE(e.fecha_adquisicion, a.fecha_adquisicion),
            valor_actual      = COALESCE(e.valor_actual,      a.valor_actual),
            descripcion       = COALESCE(e.descripcion,       a.descripcion)
        FROM activos a
        WHERE e.activo_id = a.id_activo;

        RAISE NOTICE 'Datos migrados desde activos hacia equipos';
    ELSE
        RAISE NOTICE 'Migración de activos omitida: tabla o columna activo_id ausente';
    END IF;
END $$;

-- 3. Eliminar columna obsoleta equipos.estado (boolean, no mapeada en EquiposJpa)
ALTER TABLE equipos DROP COLUMN IF EXISTS estado;

-- 4. Eliminar columna obsoleta custodias.fk_ubicacion (no mapeada en CustodiasJpa)
ALTER TABLE custodias DROP COLUMN IF EXISTS fk_ubicacion;

-- 5. Eliminar tablas legacy (actualizacion_activos primero por FK hacia activos)
DROP TABLE IF EXISTS actualizacion_activos CASCADE;
DROP TABLE IF EXISTS activos              CASCADE;
