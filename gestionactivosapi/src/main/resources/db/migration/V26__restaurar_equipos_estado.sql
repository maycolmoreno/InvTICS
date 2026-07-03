-- V26: Restaurar equipos.estado (boolean) eliminado incorrectamente en V25
-- EquiposJpa mapea este campo; la herramienta tools/migracion_eliminar_activos.sql
-- lo dropeo prematuramente antes de que la entidad fuera actualizada.

ALTER TABLE equipos ADD COLUMN IF NOT EXISTS estado BOOLEAN;
