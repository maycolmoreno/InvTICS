DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'notificaciones'
          AND constraint_name = 'fk_notificaciones_mantenimiento'
    ) THEN
        ALTER TABLE notificaciones
            ADD CONSTRAINT fk_notificaciones_mantenimiento
            FOREIGN KEY (referencia_mantenimiento_id)
            REFERENCES mantenimientos(id_mantenimiento)
            ON DELETE SET NULL;
    END IF;
END $$;
