-- V28: Campos de trazabilidad en movimientos_inventario
-- Agrega condicion (estado fisico), realizado_por (quien ejecuto), motivo y fecha_efectiva
ALTER TABLE movimientos_inventario
    ADD COLUMN IF NOT EXISTS condicion      VARCHAR(50),
    ADD COLUMN IF NOT EXISTS realizado_por  VARCHAR(200),
    ADD COLUMN IF NOT EXISTS motivo         TEXT,
    ADD COLUMN IF NOT EXISTS fecha_efectiva DATE;
