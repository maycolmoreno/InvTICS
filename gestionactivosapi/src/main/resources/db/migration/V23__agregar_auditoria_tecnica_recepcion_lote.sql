-- Auditoría técnica obligatoria de recepción en recepciones_lote

-- 1. Añadir columnas como nullable primero
ALTER TABLE recepciones_lote
    ADD COLUMN IF NOT EXISTS uuid             VARCHAR(60),
    ADD COLUMN IF NOT EXISTS recepcionado_por VARCHAR(100),
    ADD COLUMN IF NOT EXISTS recepcionado_en  TIMESTAMP(6);

-- 2. Rellenar filas existentes con valores seguros antes de imponer NOT NULL
UPDATE recepciones_lote
SET uuid             = 'legacy-' || id_recepcion_lote,
    recepcionado_por = COALESCE(created_by, 'sistema'),
    recepcionado_en  = COALESCE(created_at, NOW())
WHERE uuid IS NULL;

-- 3. Imponer NOT NULL una vez que los datos existentes son consistentes
ALTER TABLE recepciones_lote
    ALTER COLUMN uuid             SET NOT NULL,
    ALTER COLUMN recepcionado_por SET NOT NULL,
    ALTER COLUMN recepcionado_en  SET NOT NULL;

-- 4. Índice único sobre uuid
CREATE UNIQUE INDEX IF NOT EXISTS ux_recepciones_lote_uuid ON recepciones_lote (uuid);
