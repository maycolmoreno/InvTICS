-- V19: Invert departamentos ↔ ubicaciones relationship
-- Idempotente: cada paso verifica el estado real antes de actuar.

-- 1. Añadir fk_departamento a ubicaciones si no existe
ALTER TABLE ubicaciones ADD COLUMN IF NOT EXISTS fk_departamento INTEGER;

-- 2. Migrar datos solo si departamentos.fk_ubicacion aún existe
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'departamentos' AND column_name = 'fk_ubicacion'
    ) THEN
        UPDATE ubicaciones u
        SET fk_departamento = d.id_departamento
        FROM departamentos d
        WHERE d.fk_ubicacion IS NOT NULL
          AND d.fk_ubicacion = u.id_ubicacion
          AND u.fk_departamento IS NULL;
    END IF;
END $$;

-- 3. Añadir FK constraint si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_ubicaciones_departamento'
    ) THEN
        ALTER TABLE ubicaciones
            ADD CONSTRAINT fk_ubicaciones_departamento
            FOREIGN KEY (fk_departamento) REFERENCES departamentos(id_departamento);
    END IF;
END $$;

-- 4. Eliminar columna fk_ubicacion de departamentos si existe
ALTER TABLE departamentos DROP COLUMN IF EXISTS fk_ubicacion;
