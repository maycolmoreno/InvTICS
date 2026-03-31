-- V19: Invert departamentos ↔ ubicaciones relationship
-- Before: departamentos.fk_ubicacion → ubicaciones (dept has ONE location)
-- After:  ubicaciones.fk_departamento → departamentos (dept has MANY locations)

-- 1. Add fk_departamento column to ubicaciones
ALTER TABLE ubicaciones ADD COLUMN fk_departamento INTEGER;

-- 2. Migrate existing data: for each departamento that pointed to a ubicacion,
--    set that ubicacion's fk_departamento
UPDATE ubicaciones u
SET fk_departamento = d.id_departamento
FROM departamentos d
WHERE d.fk_ubicacion IS NOT NULL
  AND d.fk_ubicacion = u.id_ubicacion;

-- 3. Add FK constraint
ALTER TABLE ubicaciones
    ADD CONSTRAINT fk_ubicaciones_departamento
    FOREIGN KEY (fk_departamento) REFERENCES departamentos(id_departamento);

-- 4. Drop old FK and column from departamentos
ALTER TABLE departamentos DROP COLUMN IF EXISTS fk_ubicacion;
