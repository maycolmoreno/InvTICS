# Documentacion tecnica - Inventario operativo CRESIO

## 1. Alcance

Este documento describe la implementacion tecnica del modulo de inventario operativo de CRESIO. El modulo cubre el ciclo de vida logistico de activos y consumibles:

- ordenes de compra,
- ingreso a bodega,
- stock de consumibles,
- asignacion a custodios,
- devoluciones,
- traslados entre bodegas,
- baja de activos,
- historial de movimientos.

La solucion esta integrada en dos proyectos:

- `gestionactivosapi`: API REST principal con reglas de negocio y persistencia.
- `consumogestionactivosapi`: aplicacion web MVC/Thymeleaf que consume el API.

## 2. Arquitectura

### Backend API

Proyecto:

```text
gestionactivosapi
```

Responsabilidades:

- exponer endpoints REST bajo `/api/inventario`,
- ejecutar reglas de negocio,
- persistir entidades JPA,
- registrar trazabilidad en `movimientos_inventario`,
- validar estados de activos, stock y ordenes de compra.

Clases principales:

```text
gestionactivosapi/src/main/java/com/uisrael/gestionactivosapi/aplicacion/servicios/InventarioService.java
gestionactivosapi/src/main/java/com/uisrael/gestionactivosapi/presentacion/controladores/InventarioControlador.java
```

### Frontend web

Proyecto:

```text
consumogestionactivosapi
```

Responsabilidades:

- mostrar la pantalla operativa de inventario,
- enviar formularios al API mediante `RestClient`,
- mostrar mensajes de exito/error,
- consultar movimientos y stock.

Clases principales:

```text
consumogestionactivosapi/src/main/java/com/uisrael/consumogestionactivosapi/controlador/InventarioControlador.java
consumogestionactivosapi/src/main/java/com/uisrael/consumogestionactivosapi/service/IInventarioOperacionServicio.java
consumogestionactivosapi/src/main/java/com/uisrael/consumogestionactivosapi/service/impl/InventarioOperacionServicioImpl.java
consumogestionactivosapi/src/main/resources/templates/Inventario/ingresoBodega.html
```

## 3. Modelo de datos

La migracion principal del modulo es:

```text
gestionactivosapi/src/main/resources/db/migration/V21__inventario_operacional.sql
```

Tablas nuevas:

- `bodegas`
- `consumibles`
- `ordenes_compra`
- `ordenes_compra_detalle`
- `stock_consumible_bodega`
- `movimientos_inventario`

Columnas agregadas a `equipos`:

- `codigo_cresio`
- `estado_inventario`
- `id_bodega_actual`
- `id_orden_compra`
- `fecha_garantia`
- `etiquetado`

Entidades JPA principales:

```text
BodegaJpa
ConsumibleJpa
OrdenCompraJpa
OrdenCompraDetalleJpa
StockConsumibleBodegaJpa
MovimientoInventarioJpa
EquiposJpa
```

## 4. Estados y movimientos

### Estados de inventario de activos

Enum:

```text
EstadoInventarioActivo
```

Valores:

- `EN_BODEGA`
- `ASIGNADO`
- `EN_REPARACION`
- `EN_TRANSITO`
- `DADO_DE_BAJA`

### Tipos de movimiento

Enum:

```text
TipoMovimientoInventario
```

Valores usados:

- `INGRESO_ACTIVO`
- `INGRESO_CONSUMIBLE`
- `ASIGNACION_ACTIVO`
- `ASIGNACION_CONSUMIBLE`
- `DEVOLUCION`
- `TRASLADO`
- `BAJA`

Cada operacion relevante genera un registro en `movimientos_inventario`.

## 5. Endpoints REST

Base:

```text
/api/inventario
```

Catalogos:

```http
GET  /api/inventario/bodegas
POST /api/inventario/bodegas

GET  /api/inventario/consumibles
POST /api/inventario/consumibles
```

Ordenes de compra:

```http
GET  /api/inventario/ordenes-compra
POST /api/inventario/ordenes-compra
POST /api/inventario/ordenes-compra/{id}/confirmar-recepcion
```

Recepcion:

```http
POST /api/inventario/recepcion/activos
POST /api/inventario/recepcion/consumibles
```

Asignacion / entrega:

```http
GET  /api/inventario/activos/en-bodega
POST /api/inventario/asignaciones/activos
POST /api/inventario/asignaciones/consumibles
```

Devoluciones:

```http
POST /api/inventario/devoluciones/activos
POST /api/inventario/devoluciones/consumibles
```

Traslados:

```http
POST /api/inventario/traslados/activos
POST /api/inventario/traslados/consumibles
```

Bajas:

```http
POST /api/inventario/bajas/activos
```

Consultas:

```http
GET /api/inventario/bodegas/{id}/stock
GET /api/inventario/movimientos
```

## 6. Flujo operativo

### 6.1 Orden de compra

Una OC registra:

- numero unico,
- proveedor,
- fecha de emision,
- bodega destino,
- detalles de activos o consumibles.

El estado inicial es:

```text
EMITIDA
```

Cuando se reciben items pasa a:

```text
RECIBIDA_PARCIAL
```

Solo se puede confirmar como:

```text
RECIBIDA
```

si todos los detalles tienen cantidad recibida igual a cantidad solicitada.

### 6.2 Ingreso de activo

Al recibir un activo:

- se valida que la OC contemple ese tipo de activo,
- se valida que no exceda la cantidad solicitada,
- se genera `codigo_cresio`,
- se registra en `equipos`,
- se asigna estado `EN_BODEGA`,
- se vincula a bodega y OC,
- se registra movimiento `INGRESO_ACTIVO`.

El codigo CRESIO se genera con formato:

```text
CR-{PREFIJO}-{SECUENCIAL}
```

Ejemplo:

```text
CR-LAP-0001
```

### 6.3 Ingreso de consumible

Al recibir un consumible:

- se valida que la OC contemple el consumible,
- se valida que no exceda la cantidad solicitada,
- se incrementa stock en `stock_consumible_bodega`,
- se registra movimiento `INGRESO_CONSUMIBLE`.

### 6.4 Asignacion de activo

Para asignar un activo:

- debe estar en `EN_BODEGA`,
- no debe tener custodia activa,
- debe estar etiquetado,
- se crea una custodia activa,
- el equipo cambia a `ASIGNADO`,
- se limpia `bodegaActual`,
- se registra movimiento `ASIGNACION_ACTIVO`.

### 6.5 Entrega de consumible

Para entregar consumibles:

- debe existir stock en la bodega,
- la cantidad solicitada no puede superar el stock disponible,
- se descuenta la cantidad,
- se registra custodio destino,
- se registra movimiento `ASIGNACION_CONSUMIBLE`.

### 6.6 Devolucion de activo

Para devolver un activo:

- debe existir custodia activa,
- se cierra la custodia,
- se define bodega destino,
- el estado destino puede ser `EN_BODEGA` o `EN_REPARACION`,
- se registra movimiento `DEVOLUCION`.

### 6.7 Devolucion de consumible

Para devolver consumibles:

- se registra custodio origen,
- se incrementa stock en la bodega destino,
- se registra movimiento `DEVOLUCION`.

### 6.8 Traslado entre bodegas

Activo:

- solo se trasladan activos en `EN_BODEGA`,
- origen y destino deben ser bodegas diferentes,
- se actualiza `bodegaActual`,
- se registra movimiento `TRASLADO`.

Consumible:

- origen y destino deben ser bodegas diferentes,
- se valida stock suficiente en origen,
- se descuenta origen,
- se incrementa destino,
- se registra movimiento `TRASLADO`.

### 6.9 Baja de activo

Para dar de baja:

- el activo no debe estar ya en `DADO_DE_BAJA`,
- no debe tener mantenimiento `EN_PROCESO`,
- si tiene custodia activa, se cierra,
- cambia `estadoInventario` a `DADO_DE_BAJA`,
- cambia `estadoEquipo` a `DADO_DE_BAJA`,
- se limpia `bodegaActual`,
- se registra movimiento `BAJA`.

## 7. Reglas de negocio implementadas

- No se puede recibir mas de lo solicitado en la OC.
- No se puede confirmar una OC incompleta.
- No se puede asignar un activo sin etiqueta fisica.
- No se puede asignar un activo que no este en bodega.
- No se puede asignar un activo con custodia activa.
- No se puede entregar consumible sin stock suficiente.
- No se puede trasladar entre la misma bodega.
- No se puede trasladar un activo asignado.
- No se puede dar de baja un activo con mantenimiento en proceso.

## 8. Pantalla web

Ruta:

```text
/inventario/ingreso-bodega
```

Pestanas disponibles:

- Catalogos
- Orden de compra
- Activos
- Asignacion
- Consumibles
- Devoluciones
- Traslados
- Bajas
- Movimientos
- Stock

La pantalla usa formularios HTML con Thymeleaf y envia operaciones al controlador MVC:

```text
consumogestionactivosapi/src/main/java/com/uisrael/consumogestionactivosapi/controlador/InventarioControlador.java
```

Ese controlador consume el API mediante:

```text
InventarioOperacionServicioImpl
```

## 9. Ejecucion local

### Levantar API

```powershell
cd C:\Users\mmorenos\Documents\Activos_Amc-feature-activos-amc\InvTICS\gestionactivosapi
.\tools\run-api-java17.ps1
```

Puerto por defecto:

```text
http://localhost:8084
```

### Levantar web

```powershell
cd C:\Users\mmorenos\Documents\Activos_Amc-feature-activos-amc\InvTICS\consumogestionactivosapi
.\mvnw.cmd spring-boot:run
```

Luego abrir:

```text
http://localhost:{PUERTO_WEB}/inventario/ingreso-bodega
```

## 10. Consideraciones de despliegue

En `prod`, el API usa:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Por eso la base debe tener aplicada la migracion:

```text
V21__inventario_operacional.sql
```

Antes de desplegar en produccion se debe validar:

- que Flyway este habilitado,
- que la base PostgreSQL tenga permisos para crear tablas/indices/FK,
- que existan datos base de marcas, categorias, custodios y usuarios,
- que la web apunte al host correcto del API.

## 11. Pruebas recomendadas

Flujo minimo:

1. Crear bodega.
2. Crear consumible.
3. Crear OC.
4. Recibir activo con etiqueta.
5. Recibir consumible.
6. Confirmar OC cuando este completa.
7. Asignar activo.
8. Entregar consumible.
9. Devolver activo.
10. Devolver consumible.
11. Trasladar activo.
12. Trasladar consumible.
13. Dar de baja activo.
14. Revisar movimientos.

Casos negativos:

- intentar recibir mas de lo solicitado,
- confirmar OC incompleta,
- asignar activo sin etiqueta,
- entregar mas consumibles que el stock disponible,
- trasladar activo asignado,
- trasladar a la misma bodega,
- dar de baja activo con mantenimiento en proceso.
