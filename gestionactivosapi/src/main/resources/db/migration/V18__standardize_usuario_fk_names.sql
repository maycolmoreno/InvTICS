-- Estandarizar nombres de columnas FK a usuarios
-- Patrón: rol_id para semánticos, usuario_id para genéricos, _por_id para acciones

-- 1. mantenimientos: id_usuario → tecnico_id (alinear con mantenimientos_programados.tecnico_id)
ALTER TABLE mantenimientos RENAME COLUMN id_usuario TO tecnico_id;

-- 2. custodios: fk_usuario → usuario_id (patrón genérico)
ALTER TABLE custodios RENAME COLUMN fk_usuario TO usuario_id;

-- 3. actualizacion_activos: fk_usuario_actualizacion → actualizado_por_id (patrón _por_id)
ALTER TABLE actualizacion_activos RENAME COLUMN fk_usuario_actualizacion TO actualizado_por_id;

-- Renombrar constraints FK a nombres legibles
ALTER TABLE mantenimientos DROP CONSTRAINT IF EXISTS fk6hibihkuky5qeuxukbkfcf76j;
ALTER TABLE mantenimientos
    ADD CONSTRAINT fk_mantenimientos_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios (id_usuario);

ALTER TABLE custodios DROP CONSTRAINT IF EXISTS fk6mtt22i139983sq4fwe0xjp8j;
ALTER TABLE custodios
    ADD CONSTRAINT fk_custodios_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id_usuario);

ALTER TABLE actualizacion_activos DROP CONSTRAINT IF EXISTS fkce6k5tr3hya3o2noeli5suds7;
ALTER TABLE actualizacion_activos
    ADD CONSTRAINT fk_actualizacion_activos_usuario FOREIGN KEY (actualizado_por_id) REFERENCES usuarios (id_usuario);
