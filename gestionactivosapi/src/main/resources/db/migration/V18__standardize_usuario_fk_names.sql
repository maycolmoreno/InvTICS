-- Estandarizar nombres de columnas FK a usuarios
-- Idempotente: cada operación verifica si la columna existe antes de actuar.

-- 1. mantenimientos: id_usuario → tecnico_id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'mantenimientos' AND column_name = 'id_usuario'
    ) THEN
        ALTER TABLE mantenimientos RENAME COLUMN id_usuario TO tecnico_id;
    END IF;
END $$;

-- 2. custodios: fk_usuario → usuario_id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'custodios' AND column_name = 'fk_usuario'
    ) THEN
        ALTER TABLE custodios RENAME COLUMN fk_usuario TO usuario_id;
    END IF;
END $$;

-- 3. actualizacion_activos: fk_usuario_actualizacion → actualizado_por_id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'actualizacion_activos' AND column_name = 'fk_usuario_actualizacion'
    ) THEN
        ALTER TABLE actualizacion_activos RENAME COLUMN fk_usuario_actualizacion TO actualizado_por_id;
    END IF;
END $$;

-- Renombrar constraints FK (idempotente: drop si existe, add si la columna destino existe)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk6hibihkuky5qeuxukbkfcf76j') THEN
        ALTER TABLE mantenimientos DROP CONSTRAINT fk6hibihkuky5qeuxukbkfcf76j;
    END IF;
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'mantenimientos' AND column_name = 'tecnico_id'
    ) AND NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_mantenimientos_tecnico') THEN
        ALTER TABLE mantenimientos
            ADD CONSTRAINT fk_mantenimientos_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios (id_usuario);
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk6mtt22i139983sq4fwe0xjp8j') THEN
        ALTER TABLE custodios DROP CONSTRAINT fk6mtt22i139983sq4fwe0xjp8j;
    END IF;
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'custodios' AND column_name = 'usuario_id'
    ) AND NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_custodios_usuario') THEN
        ALTER TABLE custodios
            ADD CONSTRAINT fk_custodios_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id_usuario);
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fkce6k5tr3hya3o2noeli5suds7') THEN
        ALTER TABLE actualizacion_activos DROP CONSTRAINT fkce6k5tr3hya3o2noeli5suds7;
    END IF;
    IF EXISTS (
        SELECT 1 FROM information_schema.tables WHERE table_name = 'actualizacion_activos'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'actualizacion_activos' AND column_name = 'actualizado_por_id'
    ) AND NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_actualizacion_activos_usuario') THEN
        ALTER TABLE actualizacion_activos
            ADD CONSTRAINT fk_actualizacion_activos_usuario FOREIGN KEY (actualizado_por_id) REFERENCES usuarios (id_usuario);
    END IF;
END $$;
