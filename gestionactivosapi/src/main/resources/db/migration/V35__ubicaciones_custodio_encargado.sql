-- Administrador encargado de una sucursal (ubicacion), ej. farmacia.
-- Mismo patron que bodegas.id_custodio_responsable -> custodios.
ALTER TABLE ubicaciones
    ADD COLUMN id_custodio_encargado INTEGER REFERENCES custodios(id_custodio);
