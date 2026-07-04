-- Mantenimiento programado general por farmacia: la actividad planificada
-- puede apuntar a un equipo especifico O a una ubicacion (farmacia) completa.
ALTER TABLE actividades_planificadas
    ADD COLUMN IF NOT EXISTS fk_ubicacion integer REFERENCES ubicaciones(id_ubicacion);
