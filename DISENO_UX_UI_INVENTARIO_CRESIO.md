# Diseno UX/UI - Inventario Operativo CRESIO

## 1. Vision del rediseño

El modulo de Inventario Operativo debe pasar de una pantalla funcional basada en pestañas a una experiencia corporativa orientada a procesos, trazabilidad y accion rapida.

La nueva experiencia debe comportarse como un centro operativo de activos TI, inspirado en herramientas como ServiceNow Asset Management, Jira Service Management, Freshservice, ManageEngine Asset Explorer, Monday y Linear, pero manteniendo la arquitectura actual:

- Java 17
- Spring Boot
- PostgreSQL
- Flyway
- API REST
- MVC
- Thymeleaf
- Bootstrap 5

No se cambia la logica de negocio existente. Tampoco se modifican base de datos, endpoints REST, servicios existentes ni arquitectura backend. El rediseño se concentra en:

- UX,
- UI,
- navegacion,
- layouts,
- componentes,
- arquitectura visual,
- experiencia operativa.

Se reorganiza la experiencia de usuario alrededor del ciclo operativo:

```text
Comprar -> Recibir -> Etiquetar -> Asignar -> Mantener -> Devolver -> Trasladar -> Dar de baja
```

## 1.1 Analisis critico del estado actual

### Problemas de UX

- El modulo concentra demasiadas operaciones en una sola pantalla con muchas pestañas.
- El usuario debe entender el sistema desde la estructura tecnica, no desde el proceso operativo.
- Las acciones principales compiten visualmente con catalogos, stock, movimientos y reportes.
- No existe una ruta guiada para tareas complejas como recepcion de activos.
- La trazabilidad existe, pero no aparece como una historia legible para auditoria.

### Problemas de UI

- Predomina la tabla como patron principal, incluso cuando el usuario necesita decidir, priorizar o ejecutar.
- Los estados no tienen suficiente jerarquia visual.
- Las acciones no estan agrupadas por intencion: recibir, asignar, devolver, trasladar, dar de baja.
- Los formularios largos obligan a leer demasiados campos sin progresion.
- La pantalla no diferencia claramente entre consulta, configuracion y operacion.

### Problemas de navegacion

- `Inventario` mezcla vistas de reporte con operaciones logisticas.
- `Ingreso a bodega` se convirtio en contenedor de todo el flujo.
- No hay una separacion clara entre activos, inventario operativo, mantenimiento y reportes.
- Para usuarios nuevos, el menu no explica el ciclo real del trabajo.

### Problemas de arquitectura de informacion

- Catalogos, operaciones, movimientos y stock aparecen en el mismo nivel.
- Los activos no tienen un centro unico de consulta tipo expediente.
- Las custodias se entienden como registros, no como relacion viva colaborador-activos-evidencias.
- Los movimientos se ven como datos, no como una linea de tiempo auditable.

### Problemas de productividad operativa

- Muchas acciones requieren navegar por pantallas densas.
- Falta entrada rapida a tareas frecuentes: recibir, asignar, trasladar, dar de baja.
- Falta priorizacion de trabajo pendiente: OC incompletas, stock critico, activos sin etiqueta.
- Falta una vista ejecutiva que indique donde actuar primero.

### Problemas de escalabilidad visual

- Con 650+ equipos, una vista solo en cards seria lenta para usuarios administrativos.
- Una vista solo en tabla es eficiente, pero poco expresiva para usuarios operativos.
- Se necesita una vista hibrida: cards para exploracion y tabla para gestion masiva.
- Los filtros deben estar siempre disponibles y permitir busqueda por codigo, serial, custodio, ubicacion y estado.

## 2. Principios de experiencia

- Priorizar acciones frecuentes sobre consultas pasivas.
- Reducir pantallas con tablas densas.
- Mostrar contexto antes de pedir datos.
- Hacer visibles los estados del activo en todo momento.
- Mantener trazabilidad completa sin obligar a buscar en varias secciones.
- Usar componentes tipo cards, timelines, wizards, kanban y paneles laterales.
- Evitar formularios largos sin progresion.
- Separar procesos operativos de reportes historicos.
- Mantener una ruta clara para auditoria y control.

## 3. Nueva arquitectura de navegacion

### Propuesta de menu lateral

```text
Inicio

Activos TI
  - Inventario de equipos
  - Custodias
  - Ubicaciones
  - Expedientes

Inventario Operativo
  - Dashboard
  - Compras
  - Recepcion
  - Stock
  - Movimientos
  - Traslados
  - Bajas

Mantenimiento
  - Ordenes
  - Kanban
  - Planificacion
  - Checklist
  - Evidencias

Reportes
  - Inventario general
  - Por sucursal
  - Por departamento
  - Por custodio
  - Movimientos
  - Auditoria

Administracion
  - Usuarios
  - Departamentos
  - Cargos
  - Marcas
  - Categorias
  - Bodegas
  - Consumibles
```

### Justificacion

`Activos TI` agrupa la informacion viva del parque tecnologico: equipos, custodios, ubicaciones y expedientes.

`Inventario Operativo` agrupa procesos logisticos: compras, recepcion, stock, movimientos, traslados y bajas.

`Mantenimiento` queda separado porque responde a otra logica operativa: ordenes, tareas, evidencias, cierre tecnico y planificacion.

`Reportes` concentra consultas agregadas, evitando que el usuario use pantallas operativas como reportes.

`Administracion` contiene catalogos y configuracion, lejos de los flujos diarios.

## 4. Dashboard operativo

Ruta propuesta:

```text
/inventario/dashboard
```

### Objetivo

Dar una lectura rapida del estado operativo del inventario y permitir entrar a acciones prioritarias.

### Layout

```text
┌───────────────────────────────────────────────────────────────┐
│ Inventario Operativo                         [Nueva OC] [Recibir] │
├───────────────────────────────────────────────────────────────┤
│ [Total activos] [Asignados] [En bodega] [En reparacion] [Bajas] │
├───────────────────────────────────────────────────────────────┤
│ [OC pendientes] [OC recibidas] [Consumibles criticos] [Sin custodio] │
├───────────────────────────────┬───────────────────────────────┤
│ Distribucion por estado        │ Stock critico                 │
│ Donut / barras horizontales    │ Lista con semaforo            │
├───────────────────────────────┼───────────────────────────────┤
│ Proximos mantenimientos        │ Ultimos movimientos           │
│ Cards compactas                │ Timeline compacto             │
└───────────────────────────────┴───────────────────────────────┘
```

### KPIs principales

- Total activos
- Activos asignados
- Activos en bodega
- Activos en reparacion
- Activos dados de baja
- OC pendientes
- OC recibidas
- Consumibles criticos
- Equipos sin custodio
- Proximos mantenimientos

### Cards KPI

Cada card debe incluir:

- icono,
- valor principal,
- etiqueta,
- variacion o alerta,
- accion secundaria.

Ejemplo:

```text
┌─────────────────────────────┐
│ Icono caja                  │
│ 128                         │
│ Activos en bodega           │
│ 12 listos para asignar      │
│ [Ver disponibles]           │
└─────────────────────────────┘
```

### Alertas

Alertas sugeridas:

- OC con recepcion parcial por mas de X dias.
- Consumibles bajo stock minimo.
- Activos sin etiqueta.
- Activos en reparacion por mas de X dias.
- Equipos asignados sin acta firmada.
- Mantenimientos proximos a vencer.

## 5. Rediseño de Inventario Operativo

La pantalla actual `/inventario/ingreso-bodega` concentra demasiadas pestañas. Se propone dividirla en modulos de proceso.

### Nueva estructura

```text
Inventario Operativo
  - Dashboard
  - Compras
  - Recepcion
  - Stock
  - Movimientos
  - Traslados
  - Bajas
```

### Flujo principal

```text
Compra creada
     ↓
Recepcion iniciada
     ↓
Verificacion fisica
     ↓
Generacion codigo CRESIO
     ↓
Etiquetado
     ↓
Ingreso a bodega
     ↓
Asignacion / entrega
     ↓
Devolucion / traslado / baja
```

### Pantallas propuestas

```text
/inventario/compras
/inventario/recepcion
/inventario/stock
/inventario/movimientos
/inventario/traslados
/inventario/bajas
```

## 6. Compras

### Vista de compras

No usar una tabla gigante como vista principal. Usar lista de cards compactas con filtros laterales.

```text
┌───────────────────────────────────────────────────────────────┐
│ Compras                                      [Nueva orden]     │
├───────────────┬───────────────────────────────────────────────┤
│ Filtros       │ OC-2026-001                                   │
│ Estado        │ Proveedor: TechSupply                         │
│ Bodega        │ Estado: Recibida parcial                      │
│ Fecha         │ 4/10 items recibidos                          │
│ Proveedor     │ [Continuar recepcion] [Ver detalle]           │
│               │                                               │
│               │ OC-2026-002                                   │
│               │ Estado: Emitida                               │
│               │ [Iniciar recepcion]                           │
└───────────────┴───────────────────────────────────────────────┘
```

### Detalle de OC

Debe mostrar:

- informacion general,
- bodega destino,
- proveedor,
- progreso de recepcion,
- items solicitados,
- items recibidos,
- pendientes,
- historial.

## 7. Wizard de recepcion de activos

Ruta propuesta:

```text
/inventario/recepcion/nueva
```

### Paso 1: Seleccionar OC

Componentes:

- buscador de OC,
- lista de OC pendientes,
- chips de estado,
- resumen de bodega y proveedor.

Wireframe:

```text
┌──────────────────────────────────────────────┐
│ Recepcion de activos                         │
│ Paso 1 de 5 - Seleccionar OC                 │
├──────────────────────────────────────────────┤
│ Buscar OC [____________________]             │
│                                              │
│ OC-2026-001  Proveedor TechSupply            │
│ Bodega Loja  Pendiente 6 items               │
│ [Seleccionar]                                │
└──────────────────────────────────────────────┘
```

Validaciones:

- no permitir seleccionar OC recibida,
- advertir si la OC no tiene detalles,
- mostrar si la OC ya esta parcialmente recibida.

### Paso 2: Verificar productos

Componentes:

- lista de items esperados,
- cantidad solicitada,
- cantidad recibida,
- cantidad pendiente,
- selector de item a recibir.

```text
┌──────────────────────────────────────────────┐
│ Paso 2 de 5 - Verificar productos            │
├──────────────────────────────────────────────┤
│ Laptop Dell      Solicitado 10 | Recibido 4  │
│ Mouse USB        Solicitado 20 | Recibido 10 │
│                                              │
│ [Continuar]                                  │
└──────────────────────────────────────────────┘
```

Validaciones:

- no permitir cantidad recibida mayor a pendiente,
- no permitir item no contemplado en la OC.

### Paso 3: Generar codigos internos

Componentes:

- categoria,
- marca,
- modelo,
- serial,
- MAC,
- garantia,
- precio,
- preview del codigo CRESIO.

```text
┌──────────────────────────────────────────────┐
│ Paso 3 de 5 - Datos del activo               │
├──────────────────────────────────────────────┤
│ Categoria [Laptop] Marca [Dell]              │
│ Modelo [Latitude 5440]                       │
│ Serial [___________] MAC [___________]       │
│ Codigo estimado: CR-LAP-0048                 │
└──────────────────────────────────────────────┘
```

### Paso 4: Etiquetar

Componentes:

- codigo CRESIO grande,
- boton imprimir etiqueta,
- checklist de etiqueta fisica,
- campo observacion.

```text
┌──────────────────────────────────────────────┐
│ Paso 4 de 5 - Etiquetado                     │
├──────────────────────────────────────────────┤
│ Codigo generado                              │
│ CR-LAP-0048                                  │
│                                              │
│ [Imprimir etiqueta]                          │
│ [x] Codigo CR adherido al equipo             │
└──────────────────────────────────────────────┘
```

Validacion:

- no permitir asignacion futura si `etiquetado = false`.

### Paso 5: Confirmar ingreso

Componentes:

- resumen,
- bodega destino,
- OC,
- categoria,
- codigo CRESIO,
- serial,
- estado inicial.

Acciones:

- `Confirmar ingreso`
- `Guardar y recibir otro`
- `Cancelar`

## 8. Vista de equipos

Ruta propuesta:

```text
/activos/equipos
```

### Objetivo

Rediseñar la vista de equipos para soportar 650+ activos sin sacrificar velocidad ni claridad. No se recomienda usar solo cards. La vista debe tener un selector de modo:

- Vista Cards: exploracion visual y acciones rapidas.
- Vista Tabla: gestion masiva, ordenamiento, comparacion y exportacion.

### Layout

```text
┌───────────────────────────────────────────────────────────────┐
│ Equipos                     [Cards] [Tabla] [Nuevo] [Exportar] │
├───────────────┬───────────────────────────────────────────────┤
│ Filtros       │ [Buscar por codigo, serial, custodio...]       │
│ Estado        │                                               │
│ Categoria     │ ┌──────────┐ ┌──────────┐ ┌──────────┐        │
│ Marca         │ │ Foto     │ │ Foto     │ │ Foto     │        │
│ Bodega        │ │ CR-LAP   │ │ CR-IMP   │ │ CR-TAB   │        │
│ Custodio      │ │ Asignado │ │ Bodega   │ │ Reparac. │        │
│ Garantia      │ └──────────┘ └──────────┘ └──────────┘        │
└───────────────┴───────────────────────────────────────────────┘
```

### Vista Cards

Uso recomendado:

- revision operativa,
- consulta visual,
- asignacion rapida,
- deteccion de estados,
- trabajo por bodega o custodio.

### Card de equipo

```text
┌────────────────────────────────┐
│ [Fotografia / icono categoria] │
│ CR-LAP-0048                    │
│ Laptop Dell Latitude 5440      │
│ Estado: Asignado               │
│ Custodio: Ronald Moreno        │
│ Ubicacion: Loja                │
│ Garantia: vence en 8 meses     │
│ Ult. mant.: 01/05/2026         │
│ [Ver] [Asignar] [Mantenimiento]│
└────────────────────────────────┘
```

### Vista Tabla

Uso recomendado:

- 650+ equipos,
- ordenamiento por columna,
- exportacion,
- revision administrativa,
- busqueda exacta por serial o codigo,
- seleccion multiple.

Columnas sugeridas:

```text
Codigo | Categoria | Marca | Modelo | Serial | Estado | Custodio | Ubicacion | Bodega | Garantia | Acciones
```

Patrones:

- sticky header,
- filtros laterales persistentes,
- chips de filtros activos,
- paginacion,
- selector de densidad,
- acciones por fila en menu compacto.

### Acciones rapidas

- Ver expediente
- Asignar
- Devolver
- Trasladar
- Crear mantenimiento
- Dar de baja

## 8.1 Expediente del activo

Ruta propuesta:

```text
/activos/equipos/{idEquipo}/expediente
```

El expediente del activo debe ser el centro principal del sistema. Al abrir un equipo como `CR-LAP-0001`, el usuario debe ver toda la historia y estado actual del activo.

### Layout

```text
┌───────────────────────────────────────────────────────────────┐
│ CR-LAP-0001                         [Asignar] [Mantenimiento] │
│ Laptop Dell Latitude 5440 | Estado: Asignado                  │
├───────────────────────────────┬───────────────────────────────┤
│ Informacion general            │ Custodia actual               │
│ Foto / categoria               │ Ronald Moreno                 │
│ Serial, MAC, garantia          │ Loja - TI                     │
│ Marca, modelo, precio          │ Acta: firmada                 │
├───────────────────────────────┴───────────────────────────────┤
│ Tabs internas                                                   │
│ [Resumen] [Movimientos] [Mantenimientos] [Documentos] [Firmas] │
├───────────────────────────────────────────────────────────────┤
│ Timeline                                                        │
│ 12/04 Ingreso a bodega Loja                                     │
│ 15/04 Asignado a Ronald Moreno                                  │
│ 01/05 Mantenimiento preventivo                                  │
└───────────────────────────────────────────────────────────────┘
```

### Secciones

- Informacion general
- Custodia actual
- Historial de custodias
- Movimientos
- Mantenimientos
- Garantia
- Documentos
- Evidencias
- Firmas

### Acciones

- Asignar
- Devolver
- Trasladar
- Enviar a mantenimiento
- Dar de baja
- Descargar acta
- Ver movimientos

## 9. Expediente de custodio

Ruta propuesta:

```text
/custodias/expediente/{idCustodio}
```

### Layout

```text
┌───────────────────────────────────────────────────────────────┐
│ Ronald Moreno                         [Nueva asignacion]      │
│ TI - Loja | ronald@empresa.com | 0999999999                   │
├───────────────────────────────┬───────────────────────────────┤
│ Activos asignados             │ Consumibles entregados         │
│ - CR-LAP-0048                 │ - Mouse USB x1                 │
│ - CR-MON-0021                 │ - Teclado x1                   │
├───────────────────────────────┴───────────────────────────────┤
│ Timeline                                                       │
│ 14/04/2026 Asignacion CR-LAP-0048                              │
│ 20/04/2026 Entrega Mouse USB                                   │
│ 01/05/2026 Mantenimiento preventivo                            │
├───────────────────────────────────────────────────────────────┤
│ Firmas y evidencias                                             │
└───────────────────────────────────────────────────────────────┘
```

Debe mostrar:

- datos del colaborador,
- activos vigentes,
- consumibles entregados,
- historial,
- actas,
- firmas,
- evidencias.

## 10. Stock de consumibles

Ruta propuesta:

```text
/inventario/stock
```

### Vista tipo almacen

```text
┌───────────────────────────────────────────────────────────────┐
│ Stock de consumibles                         [Nuevo consumible]│
├───────────────┬───────────────────────────────────────────────┤
│ Filtros       │ Mouse USB                                     │
│ Bodega        │ Stock: 25 | Minimo: 10 | Estado: Verde        │
│ Estado stock  │ [Entregar] [Trasladar]                        │
│ Tipo          │                                               │
│               │ Toner HP                                      │
│               │ Stock: 3 | Minimo: 5 | Estado: Rojo           │
└───────────────┴───────────────────────────────────────────────┘
```

### Semaforo

- Verde: stock mayor al minimo.
- Amarillo: stock igual o cercano al minimo.
- Rojo: stock menor al minimo.

### Card de stock

```text
┌────────────────────────────┐
│ Mouse USB                  │
│ Bodega Loja                │
│ Stock actual: 25           │
│ Stock minimo: 10           │
│ Estado: Disponible         │
│ [Entregar] [Trasladar]     │
└────────────────────────────┘
```

## 11. Movimientos como timeline

Ruta propuesta:

```text
/inventario/movimientos
```

### No usar tabla como vista primaria

Usar timeline por activo, bodega o custodio.

```text
┌──────────────────────────────────────────────┐
│ Movimientos                                  │
│ Buscar [CR-LAP-0048]                         │
├──────────────────────────────────────────────┤
│ 14/04/2026 09:30                             │
│ Ingreso a bodega Loja                        │
│ OC-2026-001                                  │
│                                              │
│ 15/04/2026 11:10                             │
│ Asignado a Ronald Moreno                     │
│ Acta pendiente de firma                      │
│                                              │
│ 01/05/2026 15:20                             │
│ Mantenimiento preventivo                     │
│ Orden MT-00034                               │
│                                              │
│ 10/05/2026 10:00                             │
│ Devuelto a bodega Loja                       │
└──────────────────────────────────────────────┘
```

Filtros:

- activo,
- consumible,
- bodega,
- custodio,
- tipo de movimiento,
- rango de fechas.

## 12. Mantenimiento Kanban

Ruta propuesta:

```text
/mantenimiento/kanban
```

### Columnas

```text
Pendiente | En Proceso | Esperando Repuesto | Finalizado
```

### Card de mantenimiento

```text
┌────────────────────────────┐
│ MT-00041                   │
│ CR-LAP-0048                │
│ Preventivo                 │
│ Tecnico: Luis Perez        │
│ Vence: Hoy                 │
│ Prioridad: Alta            │
│ [Abrir]                    │
└────────────────────────────┘
```

### Interacciones

- drag and drop entre columnas,
- detalle en panel lateral,
- cambio de estado con confirmacion,
- adjuntar evidencias,
- registrar repuestos,
- cerrar con firma y observacion.

### Estados recomendados

- Pendiente
- En Proceso
- Esperando Repuesto
- Finalizado
- Cancelado

## 13. Design System

### Paleta

```text
Primario:    #4F46E5
Exito:       #22C55E
Advertencia: #F59E0B
Error:       #EF4444
Fondo:       #F8FAFC
Texto:       #0F172A
Texto suave: #64748B
Borde:       #E2E8F0
Superficie:  #FFFFFF
```

### Tipografia

Recomendacion:

- `Inter`
- `Source Sans 3`
- `Roboto`

Jerarquia:

```text
H1: 28px / 700
H2: 22px / 700
H3: 18px / 600
Body: 14px / 400
Small: 12px / 400
```

### Cards

Uso:

- KPIs,
- activos,
- stock,
- OC,
- mantenimiento.

Estilo:

```text
background: #FFFFFF
border: 1px solid #E2E8F0
border-radius: 8px
padding: 16px
shadow: sutil
```

### Badges

Estados:

```text
EN_BODEGA       azul
ASIGNADO        indigo
EN_REPARACION   naranja
DADO_DE_BAJA    rojo
RECIBIDA        verde
RECIBIDA_PARCIAL amarillo
EMITIDA         gris
```

### Botones

Primario:

```text
background #4F46E5
color white
```

Secundario:

```text
background white
border #CBD5E1
color #0F172A
```

Accion destructiva:

```text
background #EF4444
color white
```

### Inputs

- altura 40px,
- borde `#CBD5E1`,
- focus `#4F46E5`,
- label visible,
- mensaje de error debajo.

### Modales

Usar solo para:

- confirmaciones,
- acciones destructivas,
- formularios cortos.

Para procesos largos usar wizard o pagina dedicada.

### Iconografia

Recomendacion:

- Feather Icons,
- Bootstrap Icons,
- Lucide si se agrega dependencia.

Iconos sugeridos:

- caja: inventario,
- laptop: equipos,
- user-check: custodias,
- truck: traslados,
- clipboard: ordenes,
- wrench: mantenimiento,
- archive: stock,
- alert-triangle: alertas.

## 14. Componentes reutilizables

### `KpiCard`

Datos:

- titulo,
- valor,
- icono,
- estado,
- accion.

### `StatusBadge`

Datos:

- estado,
- tipo,
- texto.

### `AssetCard`

Datos:

- foto/icono,
- codigo,
- modelo,
- estado,
- custodio,
- ubicacion,
- garantia,
- ultimo mantenimiento.

### `TimelineItem`

Datos:

- fecha,
- tipo,
- descripcion,
- actor,
- entidad asociada.

### `WizardStep`

Datos:

- numero,
- titulo,
- estado,
- contenido.

### `FilterPanel`

Datos:

- filtros,
- chips activos,
- limpiar filtros.

### `ActionBar`

Datos:

- accion primaria,
- acciones secundarias,
- busqueda.

## 15. Recomendaciones de implementacion Thymeleaf + Bootstrap 5

### No crear una sola pantalla gigante

Dividir:

```text
templates/Inventario/dashboard.html
templates/Inventario/compras.html
templates/Inventario/recepcion.html
templates/Inventario/stock.html
templates/Inventario/movimientos.html
templates/Inventario/traslados.html
templates/Inventario/bajas.html
templates/Activos/equipos.html
templates/Activos/expedienteActivo.html
templates/Custodias/expedienteCustodio.html
templates/mantenimiento/kanban.html
```

### Crear fragmentos Thymeleaf

```text
templates/fragments/components/kpi-card.html
templates/fragments/components/status-badge.html
templates/fragments/components/filter-panel.html
templates/fragments/components/asset-card.html
templates/fragments/components/timeline.html
templates/fragments/components/action-bar.html
templates/fragments/components/view-toggle.html
templates/fragments/components/wizard-stepper.html
templates/fragments/components/empty-state.html
```

### Usar controllers separados

```text
InventarioDashboardControlador
InventarioComprasControlador
InventarioRecepcionControlador
InventarioStockControlador
InventarioMovimientosControlador
ActivosExperienciaControlador
CustodiasExpedienteControlador
MantenimientoKanbanControlador
```

### Mantener servicios existentes

No duplicar logica. La web debe seguir usando:

```text
IInventarioOperacionServicio
InventarioOperacionServicioImpl
```

### Javascript

Usar JS solo para:

- filtros dinamicos,
- busqueda local,
- wizard,
- seleccion multiple,
- preview de datos,
- timeline expandible.

La regla de negocio debe permanecer en el backend.

## 15.1 Estructura real recomendada

```text
consumogestionactivosapi/src/main/resources/templates/
  plantilla/
    plantillaui.html
  fragments/
    layout/
      sidebar.html
      topbar.html
      page-header.html
    components/
      kpi-card.html
      status-badge.html
      filter-panel.html
      asset-card.html
      timeline.html
      action-bar.html
      view-toggle.html
      wizard-stepper.html
      empty-state.html
  Inventario/
    dashboard.html
    compras.html
    recepcion.html
    stock.html
    movimientos.html
    traslados.html
    bajas.html
  Activos/
    equipos.html
    expedienteActivo.html
  Custodias/
    expedienteCustodio.html
  mantenimiento/
    kanban.html
```

```text
consumogestionactivosapi/src/main/resources/static/
  assets/
    css/
      cresio-design-system.css
      inventory.css
      assets.css
      maintenance-kanban.css
    js/
      inventory-dashboard.js
      inventory-wizard.js
      asset-view-toggle.js
      filters.js
      timeline.js
      kanban.js
```

## 15.2 Roadmap tecnico de bajo riesgo

La implementacion debe priorizar alto impacto visual con bajo riesgo tecnico. Por eso se recomienda:

1. Crear CSS y fragmentos reutilizables sin cambiar endpoints.
2. Rediseñar menu lateral con rutas existentes y nuevas rutas MVC.
3. Crear dashboard consultando servicios ya disponibles.
4. Separar la pantalla gigante en vistas Thymeleaf dedicadas.
5. Implementar wizard de recepcion usando los mismos POST actuales.
6. Rediseñar equipos con toggle cards/tabla usando la misma data existente.
7. Crear expediente del activo agregando solamente consultas desde servicios existentes.
8. Crear timeline visual sobre `/api/inventario/movimientos`.
9. Crear Kanban de mantenimiento sobre estados actuales.
10. Refactorizar gradualmente la pantalla antigua hasta dejarla como fallback o eliminarla.

## 16. Roadmap por fases

### Fase 1: Separacion de navegacion

- Crear nuevas rutas web.
- Mover `Ingreso a bodega` a pantallas dedicadas.
- Mantener endpoints actuales.
- Actualizar menu lateral.

### Fase 2: Dashboard operativo

- KPIs principales.
- Alertas.
- Ultimos movimientos.
- Stock critico.
- Proximos mantenimientos.

### Fase 3: Compras y recepcion

- Vista moderna de OCs.
- Wizard de recepcion.
- Etiquetado.
- Confirmacion visual de ingreso.

### Fase 4: Equipos tipo cards

- Rediseñar inventario de equipos.
- Filtros avanzados.
- Acciones rapidas.
- Expediente del activo.

### Fase 5: Custodias expediente

- Vista colaborador.
- Activos asignados.
- Consumibles.
- Firmas.
- Evidencias.

### Fase 6: Stock tipo almacen

- Vista por bodegas.
- Semaforo de stock.
- Stock minimo.
- Entrega y traslado desde cards.

### Fase 7: Movimientos timeline

- Timeline por activo.
- Timeline por bodega.
- Timeline por custodio.
- Filtros avanzados.

### Fase 8: Mantenimiento Kanban

- Tablero de ordenes.
- Estados visuales.
- Panel lateral.
- Evidencias y cierre.

### Fase 9: Auditoria y reportes

- Exportables.
- Reportes por estado.
- Reportes por bodega.
- Reportes por custodio.
- Reportes de movimientos.

## 16.1 Priorizacion por impacto y riesgo

### Alto impacto / bajo riesgo

- Nuevo menu lateral.
- Dashboard operativo con KPIs.
- Fragmentos `kpi-card`, `status-badge`, `filter-panel`.
- Vista de movimientos como timeline.
- Separacion visual de pantallas de inventario.

### Alto impacto / riesgo medio

- Wizard de recepcion.
- Vista equipos cards/tabla.
- Expediente del activo.
- Expediente del custodio.

### Alto impacto / riesgo alto

- Kanban con drag and drop.
- Reorganizacion completa de mantenimiento.
- Acciones masivas sobre inventario.

### Recomendacion de arranque

Empezar por:

```text
Menu lateral -> Dashboard -> Movimientos timeline -> Compras/Recepcion separadas
```

Esto mejora percepcion de producto y productividad sin tocar logica backend.

## 17. Resultado esperado

El modulo debe sentirse menos como una planilla administrativa y mas como un centro operativo de gestion de activos TI:

- rapido para ejecutar,
- claro para auditar,
- facil de entender para nuevos usuarios,
- visualmente moderno,
- alineado con herramientas ITSM empresariales,
- compatible con la arquitectura actual.
