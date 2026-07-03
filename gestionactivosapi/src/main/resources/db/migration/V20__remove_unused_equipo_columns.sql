-- Elimina columnas que ya no se usan en la entidad Equipos
ALTER TABLE equipos DROP COLUMN IF EXISTS tipo_equipo;
ALTER TABLE equipos DROP COLUMN IF EXISTS activo_id;
ALTER TABLE equipos DROP COLUMN IF EXISTS version_office;
ALTER TABLE equipos DROP COLUMN IF EXISTS union_dominio;
ALTER TABLE equipos DROP COLUMN IF EXISTS tipo_licencia_office;
ALTER TABLE equipos DROP COLUMN IF EXISTS sistema_operativo;
ALTER TABLE equipos DROP COLUMN IF EXISTS ip;
ALTER TABLE equipos DROP COLUMN IF EXISTS etiqueta_activo_fijo;
