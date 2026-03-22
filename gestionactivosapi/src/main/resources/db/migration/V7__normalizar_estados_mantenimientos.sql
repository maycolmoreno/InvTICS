UPDATE mantenimientos
SET estado_interno = 'CERRADO'
WHERE UPPER(COALESCE(estado_interno, '')) IN ('COMPLETADO', 'FINALIZADO');

UPDATE mantenimientos
SET estado_interno = 'EN_PROCESO'
WHERE UPPER(COALESCE(estado_interno, '')) IN ('EN_PROCESO', 'PROCESO');

UPDATE mantenimientos
SET estado_interno = 'PENDIENTE'
WHERE estado_interno IS NULL OR BTRIM(estado_interno) = '';

ALTER TABLE mantenimientos
    ALTER COLUMN estado TYPE VARCHAR(30),
    ALTER COLUMN estado_interno TYPE VARCHAR(30),
    ALTER COLUMN estado_general TYPE VARCHAR(20);

COMMENT ON COLUMN mantenimientos.estado_general IS 'Estado percibido del equipo o resultado funcional del mantenimiento.';
