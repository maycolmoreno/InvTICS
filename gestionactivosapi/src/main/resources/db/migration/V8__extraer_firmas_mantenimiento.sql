CREATE TABLE IF NOT EXISTS firmas_mantenimiento (
    id SERIAL PRIMARY KEY,
    id_mantenimiento INTEGER NOT NULL REFERENCES mantenimientos(id_mantenimiento) ON DELETE CASCADE,
    tipo_firma VARCHAR(20) NOT NULL,
    firma_base64 TEXT NOT NULL,
    firmado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_origen VARCHAR(45),
    CONSTRAINT chk_firmas_mantenimiento_tipo CHECK (tipo_firma IN ('CUSTODIO', 'TECNICO'))
);

INSERT INTO firmas_mantenimiento (id_mantenimiento, tipo_firma, firma_base64, firmado_en)
SELECT id_mantenimiento, 'TECNICO', firma_tecnico, COALESCE(fec_cierre, creado_en, CURRENT_TIMESTAMP)
FROM mantenimientos
WHERE firma_tecnico IS NOT NULL
  AND BTRIM(firma_tecnico) <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM firmas_mantenimiento f
      WHERE f.id_mantenimiento = mantenimientos.id_mantenimiento
        AND f.tipo_firma = 'TECNICO'
  );

INSERT INTO firmas_mantenimiento (id_mantenimiento, tipo_firma, firma_base64, firmado_en)
SELECT id_mantenimiento, 'CUSTODIO', firma_custodio, COALESCE(fec_cierre, creado_en, CURRENT_TIMESTAMP)
FROM mantenimientos
WHERE firma_custodio IS NOT NULL
  AND BTRIM(firma_custodio) <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM firmas_mantenimiento f
      WHERE f.id_mantenimiento = mantenimientos.id_mantenimiento
        AND f.tipo_firma = 'CUSTODIO'
  );

ALTER TABLE mantenimientos DROP COLUMN IF EXISTS firma_tecnico;
ALTER TABLE mantenimientos DROP COLUMN IF EXISTS firma_custodio;
