ALTER TABLE ordenes_compra
    ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE ordenes_compra
    DROP CONSTRAINT IF EXISTS ordenes_compra_estado_check;

ALTER TABLE ordenes_compra
    ADD CONSTRAINT ordenes_compra_estado_check
    CHECK (estado IN (
        'BORRADOR',
        'EMITIDA',
        'RECEPCION_PARCIAL',
        'RECIBIDA_PARCIAL',
        'RECIBIDA',
        'CANCELADA'
    ));

ALTER TABLE ordenes_compra_detalle
    DROP CONSTRAINT IF EXISTS ordenes_compra_detalle_tipo_item_check;

ALTER TABLE ordenes_compra_detalle
    ADD CONSTRAINT ordenes_compra_detalle_tipo_item_check
    CHECK (tipo_item IN (
        'ACTIVO',
        'STOCK',
        'CONSUMIBLE'
    ));
