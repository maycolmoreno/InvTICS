DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    SELECT tc.constraint_name
    INTO constraint_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
      ON tc.constraint_name = kcu.constraint_name
     AND tc.table_schema = kcu.table_schema
    WHERE tc.table_name = 'custodios'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND kcu.column_name = 'fk_departamento'
    LIMIT 1;

    IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE custodios DROP CONSTRAINT %I', constraint_name);
    END IF;
END $$;

ALTER TABLE custodios DROP COLUMN IF EXISTS fk_departamento;
