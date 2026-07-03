-- Fase 2A.1: Normalizar resultado_tecnico al catálogo cerrado.
-- La columna resultado_tecnico ya existe desde V29 como VARCHAR(50) NULL.

-- Paso 1: Migrar valores legacy (UI anterior usaba nombres distintos)
UPDATE mantenimientos SET resultado_tecnico = 'SIN_FALLA'            WHERE resultado_tecnico = 'INSPECCIONADO';
UPDATE mantenimientos SET resultado_tecnico = 'SIN_INTERVENCION'     WHERE resultado_tecnico = 'MANTENIMIENTO_REALIZADO';
UPDATE mantenimientos SET resultado_tecnico = 'REQUIERE_REPUESTO'    WHERE resultado_tecnico = 'PENDIENTE_REPUESTO';
UPDATE mantenimientos SET resultado_tecnico = 'ESCALADO_A_PROVEEDOR' WHERE resultado_tecnico = 'DERIVADO_GARANTIA';
UPDATE mantenimientos SET resultado_tecnico = 'ACTUALIZADO'          WHERE resultado_tecnico = 'CONFIGURADO';

-- Paso 2: Limpiar cualquier valor libre no reconocido (texto ingresado manualmente)
UPDATE mantenimientos
SET resultado_tecnico = NULL
WHERE resultado_tecnico IS NOT NULL
  AND resultado_tecnico NOT IN (
    'REPARADO', 'SIN_FALLA', 'SIN_INTERVENCION', 'PARCIALMENTE_REPARADO',
    'REQUIERE_REPUESTO', 'ESCALADO_A_PROVEEDOR', 'IRREPARABLE', 'REQUIERE_BAJA',
    'GARANTIA_APLICADA', 'GARANTIA_RECHAZADA', 'ACTUALIZADO', 'INSTALADO'
  );

-- Paso 3: Default para OTs cerradas sin resultado (histórico)
UPDATE mantenimientos
SET resultado_tecnico = 'REPARADO'
WHERE fec_cierre IS NOT NULL
  AND resultado_tecnico IS NULL;

-- Paso 4: CHECK constraint — OTs abiertas pueden seguir con NULL
ALTER TABLE mantenimientos
    ADD CONSTRAINT chk_resultado_tecnico
    CHECK (
        resultado_tecnico IS NULL OR
        resultado_tecnico IN (
            'REPARADO',
            'SIN_FALLA',
            'SIN_INTERVENCION',
            'PARCIALMENTE_REPARADO',
            'REQUIERE_REPUESTO',
            'ESCALADO_A_PROVEEDOR',
            'IRREPARABLE',
            'REQUIERE_BAJA',
            'GARANTIA_APLICADA',
            'GARANTIA_RECHAZADA',
            'ACTUALIZADO',
            'INSTALADO'
        )
    );
