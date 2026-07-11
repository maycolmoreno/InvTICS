# CLAUDE.md — Reglas de trabajo para CRESIO

## Contexto del proyecto

CRESIO es un sistema de gestión de activos TIC, inventario operativo, compras, recepción, custodias, mantenimientos, bodegas, stock y trazabilidad.

El proyecto está dividido en dos capas principales:

```text
Browser
  ↓
consumogestionactivosapi
BFF MVC / Thymeleaf / puerto 8081
  ↓ RestClient
gestionactivosapi
API Backend / puerto 8084
  ↓
PostgreSQL
```

`consumogestionactivosapi` es un BFF MVC/Thymeleaf.
No es el backend principal de negocio.
La persistencia, reglas profundas, entidades JPA, migraciones Flyway y lógica transaccional viven principalmente en `gestionactivosapi`.

## Stack

### BFF Web

* Java 17.
* Spring Boot.
* Spring MVC.
* Thymeleaf.
* Bootstrap 5.
* RestClient hacia API backend.
* Frontend Enterprise UI con clases `cui-*`.

### Backend API

* Java 17.
* Spring Boot.
* PostgreSQL.
* JPA.
* Flyway.
* Servicios de dominio/aplicación.
* Reglas transaccionales.

## Arquitectura funcional objetivo

CRESIO debe operar como plataforma ITAM empresarial, no como sistema de formularios aislados.

Regla UX principal:

```text
Datos visibles → Selección → Acción contextual → Drawer → Confirmación
```

No usar formularios permanentes si la acción puede nacer desde una fila.

## Sistema de diseño — reglas de precedencia

Las clases Bootstrap equivalentes están **deprecadas** en este proyecto. Usar siempre CUI:

| Elemento | Usar | No usar |
|----------|------|---------|
| Botón principal | `cui-btn cui-btn-primary` | `btn btn-primary` |
| Botón secundario | `cui-btn cui-btn-secondary` | `btn btn-outline-primary btn-sm` |
| Botón peligroso | `cui-btn cui-btn-danger` | `btn btn-danger` |
| Botón advertencia | `cui-btn cui-btn-warning` | `btn btn-warning` |
| Botón ícono | `cui-icon-btn` | `btn btn-light cui-icon-btn` |
| Tabla | `table cui-table` | `table table-hover` |
| Badge | `cui-badge cui-badge-*` | `badge bg-*` |

Tokens definidos en `tokens.css` — no usar valores hardcoded de color ni fallbacks inline. Si un token falta, añadirlo a `tokens.css` antes de usarlo.

## Reglas obligatorias generales

No modificar base de datos salvo aprobación explícita.

No modificar entidades, DTOs, servicios, controladores, endpoints o migraciones sin aprobación.

No crear endpoints nuevos sin aprobación.

No cambiar mappings existentes sin aprobación.

No cambiar nombres de inputs de formularios sin verificar DTOs BFF y backend.

No romper rutas antiguas.

No tocar Flutter salvo solicitud explícita.

No tocar API backend salvo que se indique explícitamente.

No mezclar cambios visuales con cambios de dominio.

No implementar múltiples fases en una sola respuesta.

No agregar funcionalidades especulativas.

No usar datos inventados para llenar vacíos del modelo.

## Flujo de trabajo obligatorio

Antes de modificar archivos:

1. Analizar código relacionado.
2. Identificar capa afectada:

   * BFF MVC/Thymeleaf.
   * API backend.
   * BD/Flyway.
   * UI estática.
3. Explicar el problema.
4. Proponer plan.
5. Listar archivos a crear/modificar.
6. Indicar riesgos.
7. Esperar aprobación.

Después de modificar:

1. Mostrar archivos creados/modificados.
2. Explicar cambios.
3. Ejecutar:

   * `.\mvnw.cmd compile`
   * `.\mvnw.cmd test`
   * `git diff --check`
4. Reportar resultado de validaciones.
5. Reportar riesgos.
6. No continuar con la siguiente fase sin aprobación.

## Reglas de Compras y Recepción

Regla central:

```text
Compra define lo solicitado.
Recepción convierte lo recibido en activo o stock.
```

Flujo objetivo:

```text
OrdenCompra
  ↓
OrdenCompraDetalle
  ↓
RecepcionLote
  ↓
Equipo / StockConsumibleBodega
  ↓
MovimientoInventario
  ↓
Custodia / Traslado / Mantenimiento / Baja
```

No debe existir recepción libre desconectada de una OC.

No usar pantallas independientes como solución principal:

```text
Recepción de activo
Recepción de consumible
```

La recepción debe realizarse desde:

```text
Gestionar OC → Línea de detalle → Recibir
```

Cada línea de OC debe mostrar:

* Descripción.
* Tipo: `ACTIVO` o `STOCK`.
* Cantidad solicitada.
* Cantidad recibida.
* Cantidad pendiente.
* Estado.
* Acción contextual `Recibir`.

Si el detalle es `ACTIVO`:

* Genera activos individuales.
* Requiere serial.
* Debe asociarse a OC, detalle y lote de recepción.
* Debe generar código CRESIO.
* Debe quedar vinculado a bodega.
* Debe guardar condición al recibir.
* Puede registrar datos técnicos opcionales.

Si el detalle es `STOCK`:

* No genera código individual.
* Incrementa stock en bodega.
* Debe asociarse a OC, detalle y lote de recepción.
* Debe generar movimiento de inventario.

## Estados de Orden de Compra

Estados válidos:

```text
BORRADOR
EMITIDA
RECEPCION_PARCIAL
RECIBIDA
CANCELADA
```

Reglas:

* `BORRADOR` permite edición.
* `EMITIDA` permite recepción.
* `RECEPCION_PARCIAL` permite recepción pendiente.
* `RECIBIDA` no permite nuevas recepciones.
* `CANCELADA` no permite operaciones.

Si en el sistema legacy existe `RECIBIDA_PARCIAL`, tratarlo como valor de compatibilidad temporal. No introducirlo en nuevas vistas como estado principal.

## Estados de OrdenCompraDetalle

Estados válidos:

```text
PENDIENTE
PARCIAL
COMPLETO
CANCELADO
```

Reglas:

* `cantidadRecibida = 0` → `PENDIENTE`.
* `0 < cantidadRecibida < cantidadSolicitada` → `PARCIAL`.
* `cantidadRecibida = cantidadSolicitada` → `COMPLETO`.
* No permitir `cantidadRecibida > cantidadSolicitada`.

## RecepcionLote

Todo lote debe tener:

* `uuid`.
* OC asociada.
* Detalle OC asociado.
* Cantidad recibida.
* Estado.
* Bodega destino.
* `recepcionadoPor`.
* `recepcionadoEn`.
* Observación opcional.

No usar `id_custodio_receptor` como responsable de recepción logística en nuevas fases. La recepción la ejecuta un usuario/custodio de bodega, pero la custodia del activo ocurre después.

## Reglas de bodegas

Una bodega debe tener custodio responsable.

El custodio responsable de bodega debe:

* Estar activo.
* Tener cargo/departamento asociado.
* Pertenecer al departamento `TECNOLOGÍAS E INNOVACIÓN`.

Validar esta regla en backend/API, no solo en Thymeleaf.

La comparación del departamento debe ser robusta:

* Ignorar mayúsculas/minúsculas.
* Evitar fallos por tildes cuando sea posible.

## Reglas de activos

Estados válidos del ciclo de vida del activo:

```text
EN_BODEGA
ASIGNADO
EN_REPARACION
EN_TRANSITO
DADO_DE_BAJA
```

Reglas de asignación:

Un activo solo puede asignarse si:

* `estadoInventario = EN_BODEGA`.
* `etiquetado = true`.
* `bodegaActual != null`.
* No tiene custodia activa.
* Custodio destino está activo.

No asignar activos:

* `ASIGNADO`.
* `EN_REPARACION`.
* `EN_TRANSITO`.
* `DADO_DE_BAJA`.

Si el activo no está etiquetado, no puede salir de bodega.

## Reglas de Stock

La pantalla Stock debe ser una consola de existencias.

No usar paneles permanentes para entregar o trasladar.

Flujo correcto:

```text
Stock → Fila de consumible/bodega → Entregar / Trasladar / Movimientos → Drawer
```

La fila debe aportar el contexto:

* Consumible.
* Bodega.
* Cantidad disponible.
* Estado.

No volver a seleccionar consumible ni bodega si la acción nace desde una fila.

## Reglas de Traslados

La pantalla Traslados debe ser principalmente:

* Historial.
* Consulta.
* Control de movimientos.

No debe tener dos formularios permanentes:

```text
Traslado de activo
Traslado de consumible
```

Los traslados deben iniciar preferentemente desde:

* Stock, si es `STOCK`.
* Listado/expediente de activo, si es `ACTIVO`.

Si existe botón global `Nuevo traslado`, debe abrir drawer/wizard, no formularios fijos paralelos.

## Reglas UX Enterprise

Evitar:

* Formularios gigantes permanentes.
* KPIs decorativos.
* Summary strips que repiten datos de la tabla.
* Botones deshabilitados sin explicación.
* Duplicidad de acciones.
* Pantallas que obligan a seleccionar datos ya visibles.

Aplicar:

* Tablas full-width.
* Acciones contextuales por fila.
* Drawers con datos precargados.
* Estados con badges.
* Progreso por línea cuando aplique.
* Empty states útiles.
* Confirmación para acciones peligrosas.
* CSS con clases `cui-*`.
* Sin estilos inline.
* Sin `onclick`/`onsubmit` inline si se puede usar JS progresivo.

## Reglas sobre KPIs y Summary Strips

Regla obligatoria:

```text
Si el usuario puede ver, filtrar o contar el dato desde la tabla,
no debe mostrarse como KPI ni Summary Strip.
```

Solo mostrar arriba:

* Riesgos.
* Pendientes.
* Excepciones.
* Alertas accionables.
* Anomalías operativas.

No mostrar:

* Totales simples.
* Activos/Inactivos.
* Cerradas/Activas si ya existe columna Estado.
* Categorías que ya existen como filtros.
* Conteos de tabla.

## Reglas de integración de empleados/custodios

Fuente: directorio institucional externo `data.cresio.com` (login por sesión Flask, credenciales vía `EMPLEADOS_SYNC_URL`/`EMPLEADOS_SYNC_USUARIO`/`EMPLEADOS_SYNC_CONTRASENA` como variables de entorno — nunca hardcodeadas en `application.properties` ni en git).

Decisión vigente (revierte el diseño original de "no consultar en vivo"): la búsqueda en vivo contra el directorio SÍ está permitida, pero **únicamente** en el flujo de Asignaciones (`/inventario/asignaciones`, que es donde se generan las custodias). Ningún otro punto de CRESIO debe consultar el directorio en vivo.

Flujo vigente:

```text
Buscar custodio en /inventario/asignaciones
  ↓
Resultado local (ya en custodios) o del directorio (aun no local)
  ↓
Si es del directorio: al seleccionarlo se crea/actualiza el custodio local
  automáticamente ("resolución al vuelo"), sin sincronización previa
  ↓
CRESIO valida la asignación contra el registro local recién creado/actualizado
```

Reglas:

* Cargo y departamento del directorio se guardan como **texto libre** en `custodios.cargo_directorio` / `custodios.departamento_directorio` — no se crean ni se exigen entradas en el catálogo propio de `cargos`/`departamentos` de CRESIO. Ese catálogo sigue existiendo para otros usos (ver regla de bodegas) y se vincula por `fk_cargo` solo si ya hay un match exacto por nombre; nunca bloquea guardar.
* No asignar activos o gastos a empleados inactivos.
* Detectar empleados fuera de servicio.
* Generar alerta si un empleado inactivo tiene activos asignados.
* Registrar fecha de última sincronización.
* Registrar cambios relevantes.
* No guardar credenciales en texto plano.

Pantalla `/custodios/sincronizacion` (sincronización por lote, solo ADMIN): se mantiene como respaldo para sincronización masiva (ej. detectar bajas), independiente de la resolución al vuelo. El editor de custodio (`/custodios/editar-custodio/{id}`) tiene un botón "Actualizar desde el directorio" que trae los datos en vivo sin guardar automáticamente.

## Reglas de BD/Flyway

No ejecutar `DROP TABLE` sin auditoría previa.

Antes de eliminar tablas:

1. Verificar si tienen datos.
2. Verificar si existen FKs.
3. Verificar entidades JPA.
4. Verificar repositorios.
5. Verificar controladores/servicios.
6. Verificar vistas que dependan de ellas.
7. Proponer migración de datos.
8. Esperar aprobación.

Tablas sospechosas actuales que requieren auditoría antes de eliminar:

* `activos`.
* `actualizacion_activos`.
* `empresas`.
* `checklist_categoria`.

No eliminar sin revisión.

## Prioridades actuales

Estado verificado contra código el 2026-07-10. Auditoría completa en memoria de sesión; no volver a tratar 1-5 como pendientes sin re-verificar código.

1. ~~Corregir drift técnico BD/JPA de compras~~ — **Hecho.** Migración V24 aplicada (`ordenes_compra.version`, check de estados, check de tipo item).
2. ~~Consolidar flujo OC → Detalle → Recepción por línea~~ — **Hecho.** Endpoints de recepción por línea + máquinas de estado (`OrdenCompraStateMachine`, `OrdenCompraDetalleStateMachine`) con tests.
3. ~~Eliminar recepción libre como flujo principal~~ — **Hecho.** `GET /inventario/recepcion` es solo redirect de compatibilidad a `/inventario/compras`.
4. ~~Rediseñar Stock (acción contextual + drawers)~~ — **Hecho.** `stock.html` usa `cui-drawer` por fila.
5. ~~Rediseñar Traslados (historial + acciones desde fila)~~ — **Hecho.** `traslados.html` idem.
6. ~~Validar bodegas con custodio de `TECNOLOGÍAS E INNOVACIÓN`~~ — **Hecho (2026-07-10).** Validación autoritativa en `InventarioService.aplicarBodega()`/`validarCustodioResponsableBodega()` (backend): custodio responsable obligatorio, debe estar activo, y su departamento debe coincidir (sin tildes/mayúsculas) con TIC — buscado tanto en el catálogo propio (`fkCargo.fkDepartamento`) como en el texto libre del directorio (`departamentoDirectorio`), ya que muchos custodios sincronizados no tienen catálogo vinculado. El BFF (`InventarioControlador.crearBodega()`) replica la misma comparación robusta como feedback rápido antes del round-trip. 5 tests nuevos en `InventarioServiceTest`.
7. ~~Completar reglas de ciclo de vida del activo~~ — **Hecho para el flujo activo (2026-07-10).** Las 5 reglas de asignación (`EN_BODEGA`, etiquetado, bodega asignada, sin custodia activa, custodio destino activo) están implementadas en `InventarioService.asignarActivosLote()` (usado por `asignarActivo` y por toda asignación real desde `/inventario/asignaciones`) y en `adoptarInventarioInicial()` (con mensajes/tipos de excepción no idénticos entre sí — deuda menor, no funcional). **Riesgo aceptado y documentado, no corregido:** el endpoint legacy `POST /api/custodias` (`CustodiasUseCaseImpl.crear`, detrás del tipo `ACTA_INICIAL`) solo valida 1 de las 5 reglas y no actualiza el estado del equipo al crear la custodia. Ninguna pantalla actual lo usa para crear (solo queda como filtro de listado histórico), pero el endpoint crudo sigue alcanzable. Decisión explícita del usuario: no tocar salvo que aparezca un bug real ahí (ver memoria de sesión).
8. Mantener UI Enterprise limpia y sin formularios redundantes — **auditoría completa (2026-07-10), remediación en curso.** ~10 pantallas cumplen (Custodias, Activos, Mantenimiento/notificaciones, Dashboard, Stock, Traslados). Violaciones restantes, de mayor a menor prioridad:
   * ~~Summary strips que duplicaban la columna Estado~~ — **Hecho (2026-07-10)** en `cargos/listarCargos.html`, `marcas/listarMarcas.html`, `departamentos/listarDepartamentos.html`, `categorias_equipo/listarCategorias.html`, `ubicaciones/listarUbicaciones.html`. Pendiente el mismo patrón en `Inventario/reparaciones.html` (no incluido en esta pasada).
   * ~~`mantenimiento/registro-manual.html`: select de custodio redundante~~ — **Mitigado (2026-07-10), sin reescribir el formulario.** El formulario sigue siendo una página completa (wizard multi-sección con checklist/firmas/evidencias, no apto para drawer sin una reescritura mayor), pero ahora acepta `?custodioId=` en `GET /mantenimiento/nuevo` y preselecciona automáticamente al custodio (incluye el caso Farmacia con sucursal). Nuevo punto de entrada por fila: botón "Crear mantenimiento" en `Custodios/listarCustodios.html` y en el dropdown de `Custodias/expedienteCustodio.html`. El dropdown de búsqueda manual se mantiene para cuando no se conoce el custodio de antemano.
   * ~~Módulo `Planificacion/*`: CSS propio desconectado de `cui-*`~~ — **Hecho (2026-07-10).** Los 3 archivos (`planificacion.html`, `planificacion-form.html`, `metricas.html`) migrados a `cui-*`/tokens, sin `<style>` propio. `planificacion.html` pasó de paginación custom a `cui-data-grid` + `data-grid.js` compartido (búsqueda/filtros en vivo, sin paginación — decisión explícita: ninguna otra pantalla del sistema pagina). Bug real encontrado y corregido de paso: los 3 formularios POST (`guardar`, `Iniciar`, `Completar`) no tenían token CSRF pese a que el sistema lo exige (`CookieCsrfTokenRepository`, sin JS global que lo inyecte) — confirmado con prueba en vivo que el POST fallaba sin el fix y funciona con él.
   * Patrón "página fija en vez de drawer" replicado en 8+ CRUDs simples: Custodios, Equipos, Roles, Usuarios, Cargos, Marcas, Departamentos, Categorías de equipo, Ubicaciones, Checklist.
   * ~~`Inventario/catalogos.html`: "Dar de baja/Reactivar" consumible sin `data-cui-confirm`~~ — **Hecho (2026-07-10).** Confirmación agregada solo para "Dar de baja" (accion destructiva); "Reactivar" no la necesita.
   * `Inventario/porSucursal.html`, `porDepartamento.html`, `porCustodio.html`: Bootstrap crudo pre-`cui-*`, KPI decorativo.
   * `importar/importarEquipos.html`: Bootstrap crudo pre-`cui-*`.
   * Bootstrap crudo puntual (`btn btn-outline-*` en vez de `cui-btn`) disperso en la mayoría de pantallas migradas.
9. Tests básicos por cada cambio funcional — **hueco real.** Bien cubierto solo en Compras/OC/Recepción e Inventario Operacional (BFF). **0 de 23 controladores del backend (`gestionactivosapi`) tienen test.** BFF: solo 4 de 29 controladores con test.
10. Resiliencia BFF:

* timeouts — **hecho** (`WebClientConfig`, `api.connect-timeout`/`api.read-timeout`).
* ControllerAdvice — **hecho** (ambos módulos).
* sesiones — solo configuración básica (`SessionCreationPolicy.IF_REQUIRED`), sin timeout explícito.
* circuit breaker — **no implementado**, condicionado a aprobación explícita (no hay dependencia de resilience4j en ningún `pom.xml`).
