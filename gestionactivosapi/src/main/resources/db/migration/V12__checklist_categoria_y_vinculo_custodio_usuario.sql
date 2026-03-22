CREATE TABLE IF NOT EXISTS checklist_categoria (
    id_actividad INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,
    PRIMARY KEY (id_actividad, id_categoria),
    CONSTRAINT fk_checklist_categoria_actividad
        FOREIGN KEY (id_actividad) REFERENCES actividades_checklist(id_actividad) ON DELETE CASCADE,
    CONSTRAINT fk_checklist_categoria_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias_equipo(id_categoria) ON DELETE CASCADE
);

ALTER TABLE custodios
    ADD COLUMN IF NOT EXISTS fk_usuario INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'custodios'
          AND constraint_name = 'fk_custodios_usuario'
    ) THEN
        ALTER TABLE custodios
            ADD CONSTRAINT fk_custodios_usuario
            FOREIGN KEY (fk_usuario)
            REFERENCES usuarios(id_usuario)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS idx_custodios_fk_usuario_unique
    ON custodios(fk_usuario)
    WHERE fk_usuario IS NOT NULL;
