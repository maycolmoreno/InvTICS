CREATE TABLE IF NOT EXISTS tickets (
    id_ticket SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    odoo_ticket_id VARCHAR(50),
    prioridad VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    tipo_origen VARCHAR(30) NOT NULL DEFAULT 'MANUAL',
    id_solicitante INTEGER,
    id_equipo INTEGER,
    id_tecnico_asignado INTEGER,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fk_mantenimiento INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT chk_tickets_prioridad CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')),
    CONSTRAINT chk_tickets_estado CHECK (estado IN ('ABIERTO', 'EN_REVISION', 'ASIGNADO', 'CERRADO', 'CANCELADO')),
    CONSTRAINT chk_tickets_tipo_origen CHECK (tipo_origen IN ('ODOO_HELPDESK', 'MANUAL')),
    CONSTRAINT fk_tickets_solicitante FOREIGN KEY (id_solicitante) REFERENCES custodios(id_custodio),
    CONSTRAINT fk_tickets_equipo FOREIGN KEY (id_equipo) REFERENCES equipos(id_equipo),
    CONSTRAINT fk_tickets_tecnico FOREIGN KEY (id_tecnico_asignado) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_tickets_mantenimiento FOREIGN KEY (fk_mantenimiento) REFERENCES mantenimientos(id_mantenimiento) ON DELETE SET NULL
);

ALTER TABLE mantenimientos
    ADD COLUMN IF NOT EXISTS fk_programado INTEGER,
    ADD COLUMN IF NOT EXISTS odoo_ticket_id VARCHAR(50),
    ADD COLUMN IF NOT EXISTS tipo_origen VARCHAR(30) NOT NULL DEFAULT 'MANUAL';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'mantenimientos'
          AND constraint_name = 'fk_mantenimientos_programados'
    ) THEN
        ALTER TABLE mantenimientos
            ADD CONSTRAINT fk_mantenimientos_programados
            FOREIGN KEY (fk_programado)
            REFERENCES mantenimientos_programados(id_programado)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_mantenimientos_origen ON mantenimientos(tipo_origen);
CREATE INDEX IF NOT EXISTS idx_mantenimientos_odoo ON mantenimientos(odoo_ticket_id);
CREATE INDEX IF NOT EXISTS idx_tickets_estado ON tickets(estado);
CREATE INDEX IF NOT EXISTS idx_tickets_equipo ON tickets(id_equipo);
CREATE INDEX IF NOT EXISTS idx_tickets_odoo ON tickets(odoo_ticket_id);
