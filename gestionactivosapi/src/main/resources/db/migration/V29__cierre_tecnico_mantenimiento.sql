-- Fase 6: persistencia real del cierre técnico
-- cerrado_en NO se crea: se mapea desde fec_cierre que ya existe.
ALTER TABLE mantenimientos
    ADD COLUMN IF NOT EXISTS resultado_tecnico VARCHAR(50),
    ADD COLUMN IF NOT EXISTS cerrado_por       VARCHAR(150);
