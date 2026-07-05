-- Cargo y departamento tal como vienen del directorio institucional externo
-- (data.cresio.com), guardados como texto libre. No dependen ni se vinculan
-- al catalogo propio de cargos/departamentos de CRESIO.
ALTER TABLE custodios
    ADD COLUMN cargo_directorio VARCHAR(150),
    ADD COLUMN departamento_directorio VARCHAR(150);
