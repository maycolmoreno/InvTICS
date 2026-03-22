DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'mantenimientos'
          AND column_name = 'sine_snapshoted'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'mantenimientos'
          AND column_name = 'sine_snapshot'
    ) THEN
        ALTER TABLE mantenimientos RENAME COLUMN sine_snapshoted TO sine_snapshot;
    END IF;
END $$;
