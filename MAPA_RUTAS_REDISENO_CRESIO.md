# Mapa de rutas - Rediseño operativo CRESIO

## Inventario Operativo

| Proceso | Ruta | Estado |
| --- | --- | --- |
| Dashboard operativo | `/inventario/dashboard` | Implementado |
| Compras | `/inventario/compras` | Implementado |
| Recepcion | `/inventario/recepcion` | Implementado |
| Stock | `/inventario/stock` | Implementado |
| Movimientos / auditoria | `/inventario/movimientos` | Implementado |
| Traslados | `/inventario/traslados` | Implementado |
| Bajas | `/inventario/bajas` | Implementado |
| Pantalla anterior | `/inventario/ingreso-bodega` | Fallback operativo |

## Activos TI

| Vista | Ruta | Estado |
| --- | --- | --- |
| Inventario de equipos cards/tabla | `/equipos` | Implementado |
| Expediente del activo | `/activos/equipos/{idEquipo}/expediente` | Implementado |

## Custodias

| Vista | Ruta | Estado |
| --- | --- | --- |
| Listado de custodias | `/custodias` | Existente, con acceso a expediente |
| Expediente del custodio | `/custodias/expediente/{idCustodio}` | Implementado |

## Mantenimiento

| Vista | Ruta | Estado |
| --- | --- | --- |
| Ordenes | `/mantenimiento` | Existente |
| Kanban | `/mantenimiento/kanban` | Implementado |
| Planificacion | `/mantenimiento/programado` | Existente |
| Nueva orden | `/mantenimiento/nuevo` | Existente |

## Reportes y auditoria

| Vista | Ruta | Estado |
| --- | --- | --- |
| Centro de reportes | `/reportes` | Rediseñado |
| Reporte de equipos | `/equipos/reporte-equipo` | Existente |
| Exportar equipos | `/equipos/reporte-equipo/excel` | Existente |
| Reporte de custodias | `/custodias/reporte` | Existente |
| Exportar custodias | `/custodias/reporte/excel` | Existente |
| Reporte de mantenimientos | `/reportes/mantenimientos` | Existente |
| Exportar mantenimientos | `/reportes/mantenimientos/excel` | Existente |
| Auditoria operativa | `/inventario/movimientos` | Implementado |

## Criterio aplicado

- No se modificaron endpoints REST.
- No se modifico base de datos.
- No se duplico logica de negocio.
- Las pantallas nuevas consumen servicios MVC/API existentes.
- La pantalla `/inventario/ingreso-bodega` queda disponible como fallback mientras se estabiliza el rediseño.
