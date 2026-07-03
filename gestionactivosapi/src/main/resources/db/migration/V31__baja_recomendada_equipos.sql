-- Fase C1: pendiente de baja persistente.
-- Al cerrar una OT con resultado IRREPARABLE o REQUIERE_BAJA, el activo queda
-- marcado hasta que la baja se ejecute (o se descarte). Sustituye al banner
-- volátil de sesión como única señal.
ALTER TABLE equipos
    ADD COLUMN IF NOT EXISTS baja_recomendada        BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS baja_recomendada_origen INTEGER;
