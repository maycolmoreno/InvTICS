ALTER TABLE mantenimientos
    ALTER COLUMN equipo_id TYPE INTEGER USING equipo_id::INTEGER,
    ALTER COLUMN cliente_id TYPE INTEGER USING cliente_id::INTEGER,
    ALTER COLUMN id_usuario TYPE INTEGER USING id_usuario::INTEGER;
