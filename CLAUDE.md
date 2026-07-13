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
8. Mantener UI Enterprise limpia y sin formularios redundantes — **auditoría completa (2026-07-10), remediación mayormente completa (2026-07-12).** Las 10 entidades del patrón drawer + los 3 reportes agrupados + importar equipos + los 2 fragments compartidos con Bootstrap crudo + limpieza cosmética de 9 pantallas más, todas migradas. Pendiente real: `ubicacionesTecnicos/historial.html`/`tiempoReal.html` (0% `cui-*`, necesitan reescritura completa) y el summary strip de `Inventario/reparaciones.html`. Violaciones ya resueltas:
   * ~~`roles/permisos.html`: form POST sin token CSRF~~ — **Hecho (2026-07-12).** Bug real, 10º de este tipo en el proyecto — el submit de "Guardar permisos" hubiera dado 403. Verificado en vivo (302 en vez de 403, estado de módulos preservado tras el submit). De paso, botones migrados a `cui-btn`.
   * ~~`ubicacionesTecnicos/consentimiento.html`: form POST sin token CSRF~~ — **Hecho (2026-07-12).** Bug real, 11º de este tipo — "Acepto el monitoreo GPS" hubiera dado 403. Verificado en vivo (302 en vez de 403; el 401 posterior del backend es autorización esperada, la pantalla es para rol TECNICO y se probó con un usuario ADMINISTRADOR, no relacionado con el fix). Resto de la pantalla (CSS `<style>` propio, sin `cui-*`) queda sin migrar — fix mínimo, solo el bug de CSRF.
   * ~~`Custodias/actaEntrega.html`: tabla desalineada~~ — **Hecho (2026-07-12).** Bug de marcado real (no CSRF): el `<thead>` tenía 4 columnas pero cada fila renderizaba 5 `<td>` (faltaba el `<th>` de "Ubicación"), y el `colspan` del estado vacío no coincidía con ninguno de los dos conteos. Corregido agregando la columna faltante y ajustando el colspan a 5. De paso, tabla y botón migrados a `cui-table`/`cui-btn`.
   * **Auditoría completa de las 14 pantallas nunca migradas (2026-07-12)**, hecha con un subagente de exploración de solo lectura. Detalle y estado tras la limpieza cosmética del mismo día:
     * ~~`Custodias/actaSalida.html`~~ — **Hecho.** Además del `colspan` (6→5, mismo bug que en `actaEntrega.html`, no reportado por el subagente pero encontrado al corregir), botones y tabla migrados a `cui-btn`/`cui-table`.
     * ~~`Custodias/expedienteCustodio.html`~~ — **Hecho.** Solo 2 botones sueltos (ya era buen ejemplo de migración `cui-*` en el resto).
     * ~~`Inventario/bajas.html`, `stock.html`, `traslados.html`~~ — **Hecho.** 1-3 botones sueltos cada una, migrados a `cui-btn`. `traslados.html`: "Confirmar llegada" usaba `btn-outline-success`, sin equivalente `cui-btn-success` — se usó `cui-btn-primary` (mismo criterio ya aplicado en Equipos para acciones de "activar").
     * ~~`Inventario/compras.html`~~ — **Hecho.** Botones y 2 badges (`bg-primary-subtle`/`bg-secondary-subtle` → `cui-badge-info`/`cui-badge-muted`) migrados. Se dejaron sin tocar 2 botones `btn btn-link text-danger p-0` (quitar fila de un template JS) — sin equivalente `cui-*` para un icon-link minimalista, mismo criterio que los toggles de `gestionarOC.html`.
     * ~~`Inventario/gestionarOC.html`~~ — **Hecho parcial.** 2 botones "Recibir stock/activo" migrados a `cui-btn`. Los 2 `btn btn-link` de toggle "Datos técnicos/financieros" se dejaron (aceptables, sin equivalente cui-* para link-utilitario). **Sin tocar, documentado como riesgo:** sus 2 `<form>` de recepción no tienen `action` estático (se asigna 100% por JS al abrir el drawer) — funciona por diseño actual pero es frágil; no se tocó sin pedirlo explícitamente.
     * ~~`roles/permisos.html`~~, ~~`ubicacionesTecnicos/consentimiento.html`~~ — ver bugs de CSRF arriba (permisos.html también migró sus 2 botones a `cui-btn`; consentimiento.html quedó con fix mínimo, resto de la pantalla sin migrar).
     * ~~`reportes/reporte-mantenimientos.html`~~ — **Hecho.** Se quitó el KPI "Registros" (duplicaba el contador `cui-data-grid-meta` de la tabla, mismo patrón corregido en 5 pantallas el 2026-07-10) y se migró el botón "Limpiar" (`btn-light cui-btn-soft` → `cui-btn cui-btn-secondary`).
     * **Sin migrar, decisión explícita de no tocar:** `ubicacionesTecnicos/historial.html` y `tiempoReal.html` (0% `cui-*`, la deuda más pesada de las 14 — necesitan una reescritura completa, no un cambio de clases, se dejan para una sesión aparte si se decide abordarlas) y `error/backend-error.html` (página standalone que **no carga `components.css`** — donde viven las clases `cui-*` — así que aplicar esas clases ahí dejaría los botones sin estilo; es una excepción deliberada, no deuda).
     * Verificado en vivo (2026-07-12): las 9 pantallas modificadas cargan con 200 y sin errores de Thymeleaf (incluida `reporte-mantenimientos.html` tras quitar la sección del KPI). 20/20 tests del BFF siguen pasando.
   * ~~Summary strips que duplicaban la columna Estado~~ — **Hecho (2026-07-10)** en `cargos/listarCargos.html`, `marcas/listarMarcas.html`, `departamentos/listarDepartamentos.html`, `categorias_equipo/listarCategorias.html`, `ubicaciones/listarUbicaciones.html`. Pendiente el mismo patrón en `Inventario/reparaciones.html` (no incluido en esta pasada).
   * ~~`mantenimiento/registro-manual.html`: select de custodio redundante~~ — **Mitigado (2026-07-10), sin reescribir el formulario.** El formulario sigue siendo una página completa (wizard multi-sección con checklist/firmas/evidencias, no apto para drawer sin una reescritura mayor), pero ahora acepta `?custodioId=` en `GET /mantenimiento/nuevo` y preselecciona automáticamente al custodio (incluye el caso Farmacia con sucursal). Nuevo punto de entrada por fila: botón "Crear mantenimiento" en `Custodios/listarCustodios.html` y en el dropdown de `Custodias/expedienteCustodio.html`. El dropdown de búsqueda manual se mantiene para cuando no se conoce el custodio de antemano.
   * ~~Módulo `Planificacion/*`: CSS propio desconectado de `cui-*`~~ — **Hecho (2026-07-10).** Los 3 archivos (`planificacion.html`, `planificacion-form.html`, `metricas.html`) migrados a `cui-*`/tokens, sin `<style>` propio. `planificacion.html` pasó de paginación custom a `cui-data-grid` + `data-grid.js` compartido (búsqueda/filtros en vivo, sin paginación — decisión explícita: ninguna otra pantalla del sistema pagina). Bug real encontrado y corregido de paso: los 3 formularios POST (`guardar`, `Iniciar`, `Completar`) no tenían token CSRF pese a que el sistema lo exige (`CookieCsrfTokenRepository`, sin JS global que lo inyecte) — confirmado con prueba en vivo que el POST fallaba sin el fix y funciona con él.
   * ~~Patrón "página fija en vez de drawer"~~ — **Hecho (2026-07-10/11). Las 10 entidades migradas**: Custodios, Cargos, Marcas, Departamentos, Categorías de equipo, Ubicaciones, Roles, Usuarios, Equipos y Checklist. Patrón consistente en las diez: `nuevoX.html`/`editarX.html` eliminados, fusionados en un único `<aside class="cui-drawer">` dentro de `listarX.html`, precargado desde `data-*` en la fila (mismo patrón que Stock/Traslados). Rutas `GET .../nuevo-x` y `.../editar-x/{id}` se conservan como redirects de compatibilidad. Errores de validación del servidor pasan a mostrarse como toast (primer error) en vez de mensaje por campo. **Bug de CSRF encontrado y corregido en Cargos, Marcas, Departamentos, Categorías de equipo y Ubicaciones** (mismo patrón que en Planificación): los formularios de alta/edición/"Desactivar" no tenían token CSRF y hubieran dado 403. Nota Ubicaciones: única entidad con un widget no trivial (mapa Leaflet con clic-para-fijar lat/lng); decisión explícita del usuario fue meter el mapa dentro del drawer de 480px (no mantenerlo en página completa) — funciona con `map.invalidateSize()` tras abrir el drawer, igual que en las páginas originales. Nota Marcas: a diferencia de Cargos/Custodios, el controlador original usaba rutas separadas (`POST /marcas`, `POST /marcas/actualizar/{id}`) con validación vía `try/catch(IllegalArgumentException)` alrededor del servicio; se consolidó en un único `POST /marcas` (como Cargos) que decide crear/actualizar según `idMarca`, conservando la validación de nombre duplicado del servicio. `POST /marcas/eliminar/{id}` sigue siendo un DELETE físico contra el backend (comportamiento preexistente, no modificado). Nota Departamentos: además del `listarDepartamentos.html` original usar markup de toolbar/empty-state propio (no la fragment compartida `data-grid-toolbar`), se migró también a la toolbar compartida de paso, consistente con Cargos/Marcas.
   * **Nota Checklist (2026-07-11):** listado más alejado del patrón (CSS `<style>` propio, summary strip decorativo, `onsubmit="return confirm(...)"` inline, CSRF faltante, agrupado visual por categoría en secciones separadas). Con aprobación explícita se reemplazó el agrupado por un único `cui-data-grid` (sin filtro de categoría — ver siguiente nota; `listarActivas()` del backend solo devuelve activas, así que tampoco hay filtro de estado, todas las filas son "Activa" por definición).
   * **Bug real de dominio encontrado y corregido (2026-07-12), con aprobación explícita del usuario tras investigar:** el campo "Categoria" (texto libre) del formulario de checklist nunca estuvo conectado a nada — `ActividadChecklistJpa` no tiene columna de texto `categoria`, solo una relación `@ManyToMany` contra `CategoriaEquiposJpa` vía la tabla `checklist_categoria` (la misma tabla ya marcada como sospechosa en la sección de BD). `ActividadChecklistRepositorioImpl.toDomain()` hacía `setCategoria(null)` a propósito y `toEntity()` nunca escribía nada — el dato se validaba como obligatorio, se aceptaba, y se descartaba siempre, tanto al guardar como al leer. Verificado en vivo antes del fix: `checklist_categoria` tenía 0 filas, y `MantenimientoControlador.actividadesBase()` usa `listarActivas()` sin filtrar por categoría — el endpoint `GET /categoria/{id}` que sí hace el join correcto (y un caso de uso completo, `ObtenerActividadesPorCategoriaUseCase`, ya construido y registrado como bean) nunca se invoca desde ningún lado del BFF. Es decir: alguien empezó a migrar hacia la relación normalizada (el javadoc de `ObtenerActividadesPorCategoriaUseCase` dice literalmente "en lugar del campo de texto libre 'categoria' (ya eliminado)") pero nunca terminó — ni conectó el nuevo caso de uso a un endpoint, ni quitó el campo viejo roto. **Decisión: se eliminó el campo `categoria` de texto libre** en ambos proyectos (dominio `ActividadChecklist`, DTOs request/response de ambos módulos, validación del use case, controlador REST, controlador BFF, template) — **sin tocar** el modelo `@ManyToMany`/`checklist_categoria`/`ObtenerActividadesPorCategoriaUseCase`/el endpoint `GET /categoria/{id}`, que quedan como infraestructura correcta pero sin usar, pendiente de una decisión de producto futura (¿UI de multi-select contra el catálogo de Categorías de equipo? ¿cablear mantenimiento para filtrar checklist por categoría del equipo?). Dos usos adicionales del getter roto encontrados y corregidos de paso: `ObtenerOrdenTrabajoUseCaseImpl` (backend) y `MantenimientoControlador.actividadesBase()/construirActividadesSeleccionadas()` (BFF) — ambos pasan a `null`/omiten el campo, mismo comportamiento efectivo que antes (siempre había sido null). `crescio_mobile` lee `categorias`/`categoria` del JSON con fallback seguro (`_primeraCategoria()`), así que no se toca Flutter y el cambio de contrato (el campo ya no aparece en el JSON) no le afecta — antes también recibía siempre lista vacía. Verificado en vivo tras el fix: alta y edición sin categoría, y que `/mantenimiento/nuevo` sigue cargando el checklist correctamente. Detalle completo en `project_checklist_categoria_roto.md`.
   * **Nota Equipos (2026-07-11):** el más grande del lote (19 campos del DTO, ~14 expuestos en el formulario). Con aprobación explícita se migró todo al drawer, agrupado en secciones ("Informacion principal" + "Hardware, red y licencias"). El listado (`listarEquipos.html`) ya usaba `cui-*` y paginación de servidor (única pantalla que pagina — no se tocó esa decisión, es ortogonal al patrón drawer). El campo "Estado equipo" preserva la inconsistencia original: texto libre en alta, `<select>` fijo (OPERATIVO/MANTENIMIENTO/BAJA) en edición — dos inputs con el mismo `name="estadoEquipo"`, uno `disabled` según el modo para que solo uno se envíe. **Importante:** a diferencia de las demás entidades, `GET /equipos/editar-equipo/{id}` NO quedó como redirect plano a la lista — `Activos/expedienteActivo.html` tiene 3 enlaces reales a esa ruta (no solo compatibilidad hacia atrás), así que ahora redirige a `/equipos?editarEquipo={id}` y un script en `listarEquipos.html` detecta el query param al cargar, ubica la fila (`tr[data-equipo-id]`) y abre el drawer preseleccionado vía `window.CresioDrawer.open()` — mismo patrón de deep-link ya usado en `mantenimiento/nuevo?custodioId=`. CSRF faltante corregido. Verificado en vivo: alta, serial duplicado, MAC inválida, edición, y que la ruta `editar-equipo/{id}` redirige correctamente con el query param (la apertura del drawer vía JS no se pudo verificar en navegador real dentro de esta sesión, solo por revisión de código — vale la pena una prueba manual rápida).
   * **Nota Usuarios (2026-07-10):** entidad más sensible del lote (contraseña + permisos). Con aprobación explícita del usuario se migró TODO al drawer, incluido el bloque "Módulos asignados al rol" (fetch/PUT en vivo a `/api/relaciones/modulos-por-rol/{rolId}`, escribe permisos del rol completo al instante, independiente del submit del usuario) — se trasladó tal cual, sin tocar su lógica ni sus endpoints. Password: en alta es obligatoria (`required` + regex de complejidad); en edición es opcional ("dejar en blanco para no cambiar"), igual que antes. El estado del usuario se maneja con un input hidden sincronizado por JS (no un checkbox con `name="estado"` directo) porque, a diferencia de Cargos/Departamentos, el controlador no fuerza `estado=true` en el alta — antes lo hacía un `<input type="hidden" name="estado" value="true">` fijo en `nuevoUsuario.html`; en el drawer unificado ese valor lo controla el JS según el modo (alta vs edición) para no duplicar el parámetro `estado`. CSRF faltante corregido igual que en las demás entidades. Verificado en vivo: alta, cédula inválida, edición sin cambiar contraseña, desactivar, y que el endpoint de módulos-por-rol responde igual que antes.
   * **Nota Roles (2026-07-10):** `permisos.html`/`GET|POST /roles/permisos/{id}` (asignación de módulos por checkboxes) se dejó intacto como página propia — no aplica al patrón drawer, es un flujo de asignación masiva. Al probar la migración se confirmó que `RolesControlador`/backend **no valida nombres duplicados** (a diferencia de Cargos/Departamentos/Marcas/Ubicaciones, que sí lo hacen vía `nombreExiste`) — se pudo crear un segundo rol "Administrador" sin error; comportamiento preexistente, no introducido por esta migración, no corregido por estar fuera de alcance (evitar validación especulativa no pedida). Además el `<select>` de nombre usa capitalización título ("Administrador") mientras la BD guarda mayúsculas ("ADMINISTRADOR") — el preselect en modo edición no coincide y el select vuelve a "Seleccione un rol"; mismo bug ya existía en el `editarRol.html` original (comparación exacta de Thymeleaf), no introducido aquí.
   * **Bug real de backend encontrado durante la migración de Categorías de equipo (2026-07-10), corregido con aprobación explícita del usuario:** `CategoriaEquiposUseCaseImpl.eliminar()` en `gestionactivosapi` no tenía `@CacheEvict(value = "categorias", allEntries = true)` — a diferencia de `crear()`/`actualizar()` y de los métodos equivalentes en Cargos/Departamentos/Marcas/Ubicaciones/Roles (todos evictan correctamente). Efecto: al desactivar una categoría la BD quedaba correcta pero `/categorias-equipo` seguía mostrando la fila como activa hasta que expiraba el caché. Fix de una línea. **Confirmado en vivo (2026-07-12) que ya está activo** — el proceso de `gestionactivosapi` fue reiniciado.
   * ~~`Inventario/catalogos.html`: "Dar de baja/Reactivar" consumible sin `data-cui-confirm`~~ — **Hecho (2026-07-10).** Confirmación agregada solo para "Dar de baja" (accion destructiva); "Reactivar" no la necesita.
   * ~~`Inventario/porSucursal.html`, `porDepartamento.html`, `porCustodio.html`: Bootstrap crudo pre-`cui-*`, KPI decorativo~~ — **Hecho (2026-07-12).** Migradas a `cui-*` (page-header, `cui-card` por grupo, `cui-table`, `cui-badge`). El badge "Total equipos asignados" del header (KPI decorativo/total simple) se eliminó; cada card de grupo ya muestra su propio conteo. La estructura agrupada (una card por sucursal/departamento/custodio) se mantuvo — es la razón de ser de estas 3 pantallas, no un `cui-data-grid` plano.
   * ~~`importar/importarEquipos.html`: Bootstrap crudo pre-`cui-*`~~ — **Hecho (2026-07-12).** Migrado a `cui-*` (page-header, `cui-card`, `cui-btn`, `cui-badge`, `cui-table`). Bug real encontrado y corregido de paso: los 3 formularios POST (`/importar/preview`, `/importar/confirmar`, `/importar/cancelar`) no tenían token CSRF — mismo patrón encontrado 9 veces antes en esta sesión.
   * ~~Bootstrap crudo puntual (`btn btn-outline-*` en vez de `cui-btn`) disperso en la mayoría de pantallas migradas~~ — **Hecho (2026-07-12)** para las 10 entidades del patrón drawer. La causa real era doble: (1) el patrón "Editar" que se copió de Cargos a las demás 9 entidades sin corregir, y (2) dos fragments compartidos (`fragments/components/confirm-modal.html`, usado por todo `data-cui-confirm` del sistema; `fragments/components/data-grid-toolbar.html`, usado por todo `cui-data-grid`) que aún tenían botones `btn btn-light`/`btn btn-outline-primary`/`.cui-btn-soft` — corregir esos 2 fragments limpió automáticamente la mayoría de las pantallas migradas sin tocarlas una por una. **Pendiente real, sin migración completa a `cui-*` todavía** (solo se corrigieron los bugs reales de CSRF/marcado de arriba, no el resto del Bootstrap crudo cosmético): `Custodias/actaSalida.html`, `Inventario/bajas.html`/`compras.html`/`gestionarOC.html`/`stock.html`/`traslados.html`, `roles/permisos.html` (parcial), `ubicacionesTecnicos/*` (parcial en consentimiento.html), `reportes/reporte-mantenimientos.html`, `error/backend-error.html` — ver auditoría completa arriba.
9. Tests básicos por cada cambio funcional — **hueco real, remediación en curso desde 2026-07-12.** Bien cubierto solo en Compras/OC/Recepción e Inventario Operacional (BFF). **Backend (`gestionactivosapi`): 4 de 23 controladores con test.**
   * `ActividadChecklistControlador` — 7 tests (`ActividadChecklistControladorTest.java`). Cubre los 6 endpoints (listar activas, obtener por id, listar por categoría, crear, actualizar, eliminar). Incluye un test que verifica explícitamente que el DTO de respuesta **ya no tiene** los campos `categoria`/`categorias` (`jsonPath(...).doesNotExist()`) — candado de regresión sobre el fix de `project_checklist_categoria_roto.md` para que nadie reintroduzca el campo roto sin querer.
   * `CustodiosControlador` — 17 tests (`CustodiosControladorTest.java`). Cubre los 10 endpoints: alta (validación `@NotBlank`/`@CedulaEcuatoriana` → 400 real verificado), listar, obtener por id, actualizar, actualizar estado, existe-cedula/existe-correo (con y sin id de exclusión), y los 3 endpoints de directorio institucional (buscar, resolver, previsualizar) incluyendo sus ramas de error (400 por `IllegalArgumentException`/`IllegalStateException`, 404 si la persona no existe).
   * `CustodiasControlador` — 15 tests (`CustodiasControladorTest.java`). Cubre alta (documenta el comportamiento actual: **una custodia por cada equipo del arreglo**, sin las 5 reglas de asignación — mismo riesgo aceptado de `project_custodias_legacy_endpoint.md`, ahora con test que lo hace explícito en vez de solo documentado), validación `@NotNull` en fechaInicio/observacion, listar, obtener por id, actualizar, actualizar estado, registrar acta PDF, y los 3 endpoints de subir/descargar acta firmada — estos últimos con I/O real de archivos contra una carpeta temporal bajo `target/` (limpiada en `@AfterEach`), no mockeada, porque la lógica de validación (tamaño, content-type, existencia) vive directamente en el controlador.
   * `InventarioControlador` — 49 tests (`InventarioControladorTest.java`), el controlador más grande del backend (32 endpoints: bodegas, consumibles, órdenes de compra/recepciones, movimientos, listados de activos por estado, asignaciones/devoluciones/traslados/bajas, reparaciones incluido el flujo con OT, etiquetado y adopción de inventario inicial). Es un controlador delgado (delega casi todo a `InventarioService`/`ReparacionOrquestadorService`, ya bien cubiertos a nivel de servicio), así que los tests verifican wiring HTTP: códigos de estado, binding de path/query params, validación `@Valid`/`@Pattern`/`@Min` disparando 400 reales, y la extracción de correo desde `Principal` en `enviarConOt`/`retornarYCerrar` (con y sin usuario autenticado). Dos gotchas de Spring/Jackson encontrados y corregidos en el propio test (no bugs de producción): `PageImpl` construido sin `Pageable` explícito rompe la serialización JSON (`Unpaged` lanza `UnsupportedOperationException`) — hay que usar `new PageImpl<>(content, PageRequest.of(page, size), total)`; y `motivo` en baja de activo tiene un `@Pattern` restringido a un enum de texto (`DESTRUCCION|OBSOLESCENCIA|ROBO_PERDIDA|DONACION|DANIO_IRREPARABLE|OTRO`), no texto libre.
   * Patrón usado en los cuatro: `MockMvcBuilders.standaloneSetup(new Controlador(mocks...))` + Mockito, igual al ya usado en el BFF (`CustodiasExpedienteControladorTest.java`) — sin `@WebMvcTest`/contexto Spring completo, rápido. 182/182 tests del backend pasan (94 base + 17 + 15 + 49 + 7).
   * Quedan 19 controladores backend sin test. BFF: sigue en 4 de 29 controladores con test (sin cambios esta ronda).
10. Resiliencia BFF:

* timeouts — **hecho** (`WebClientConfig`, `api.connect-timeout`/`api.read-timeout`).
* ControllerAdvice — **hecho** (ambos módulos).
* sesiones — solo configuración básica (`SessionCreationPolicy.IF_REQUIRED`), sin timeout explícito.
* circuit breaker — **no implementado**, condicionado a aprobación explícita (no hay dependencia de resilience4j en ningún `pom.xml`).
