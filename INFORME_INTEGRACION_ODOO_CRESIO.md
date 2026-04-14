# INFORME TÉCNICO: Integración Odoo v17 Helpdesk ↔ CRESIO

**Fecha:** Junio 2025  
**Preparado por:** Equipo de Desarrollo CRESIO  
**Versión:** 1.0  
**Clasificación:** Uso Interno

---

## 1. RESUMEN EJECUTIVO

El presente informe detalla la propuesta de integración entre **Odoo v17 Helpdesk** (sistema de mesa de ayuda) y **CRESIO** (sistema de gestión de activos tecnológicos). El objetivo es canalizar los tickets de soporte relacionados con **daños, fallos o incidencias en equipos tecnológicos** desde Odoo hacia CRESIO, donde se gestionan con mayor detalle: evidencia fotográfica, firmas digitales, checklists técnicos, reportes PDF y trazabilidad completa.

### ¿Por qué integrar?

| Capacidad | Odoo Helpdesk | CRESIO |
|-----------|:---:|:---:|
| Recepción de tickets de usuario | ✅ | ❌ |
| SLA y escalamiento | ✅ | ❌ |
| Portal de autoservicio | ✅ | ❌ |
| Inventario detallado de equipos TI | ❌ | ✅ |
| Checklist de mantenimiento | ❌ | ✅ |
| Evidencia fotográfica | ❌ | ✅ |
| Firmas digitales (actas) | ❌ | ✅ |
| Reportes PDF/Excel de mantenimiento | ❌ | ✅ |
| Seguimiento GPS de técnicos | ❌ | ✅ |
| App móvil para técnicos | ❌ | ✅ |
| Historial detallado por equipo | ❌ | ✅ |
| Alertas push a técnicos | ❌ | ✅ |

**Conclusión:** Odoo es ideal para la **recepción y clasificación** de tickets. CRESIO es ideal para la **ejecución técnica y documentación** del trabajo. La integración combina lo mejor de ambos mundos.

---

## 2. ESTADO ACTUAL: LO QUE YA ESTÁ CONSTRUIDO

> **Hallazgo importante:** CRESIO ya tiene cimientos para la integración con Odoo.

### 2.1 Campos existentes en la base de datos

| Campo | Tabla | Tipo | Descripción |
|-------|-------|------|-------------|
| `odoo_ticket_id` | `mantenimientos` | VARCHAR(50) | ID del ticket de Odoo vinculado |
| `tipo_origen` | `mantenimientos` | VARCHAR(30) | Origen: `ODOO_HELPDESK`, `PROGRAMADO`, `MANUAL` |

### 2.2 Enumeración de origen

```java
public enum TipoOrigenMantenimiento {
    ODOO_HELPDESK,   // ← Ticket proveniente de Odoo
    PROGRAMADO,       // Mantenimiento programado
    MANUAL            // Creado manualmente
}
```

### 2.3 Validación en lógica de negocio

```java
// MantenimientosUseCaseImpl.crear()
if (mantenimiento.getTipoOrigen() == TipoOrigenMantenimiento.ODOO_HELPDESK
        && (mantenimiento.getOdooTicketId() == null || mantenimiento.getOdooTicketId().isBlank())) {
    throw new IllegalArgumentException("odooTicketId es obligatorio para mantenimientos de Odoo");
}
```

### 2.4 Endpoint de seguridad reservado

```java
// SecurityConfig.java
.requestMatchers("/api/tickets/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
```

> El path `/api/tickets/**` ya está autorizado pero **no tiene controlador implementado**. Es el punto de entrada reservado para la integración.

### 2.5 DTOs preparados

Tanto `MantenimientosRequestDTO` como `MantenimientosResponseDTO` incluyen los campos `odooTicketId` y `tipoOrigen`. Los mappers de MapStruct los mapean automáticamente.

### 2.6 Identificación de equipos

CRESIO identifica cada equipo con campos únicos que permiten la vinculación:

| Campo CRESIO | Columna BD | Uso para vinculación |
|--------------|------------|---------------------|
| `codigoSap` | `codigo_sap` | ✅ Identificador principal (código inventario) |
| `serial` | `serial` | ✅ Número de serie del fabricante |
| `mac` | `mac` | ⚠️ Secundario (puede cambiar con NIC) |

---

## 3. ARQUITECTURA DE INTEGRACIÓN PROPUESTA

### 3.1 Diagrama de flujo general

```
┌─────────────────────────────────────────────────────────────────────┐
│                        FLUJO DE INTEGRACIÓN                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  USUARIO/EMPLEADO                                                   │
│       │                                                             │
│       ▼                                                             │
│  ┌──────────┐    Ticket creado     ┌──────────────────────┐         │
│  │  ODOO    │ ──────────────────►  │ Automated Action     │         │
│  │ Helpdesk │    (tag: equipo)     │ (Server Action)      │         │
│  │  v17     │                      │ POST webhook a CRESIO│         │
│  └──────────┘                      └──────────┬───────────┘         │
│       ▲                                       │                     │
│       │                                       ▼                     │
│       │  Actualización         ┌──────────────────────────┐         │
│       │  de estado             │  CRESIO - API Backend    │         │
│       │  (XML-RPC)             │  /api/tickets/webhook    │         │
│       │                        │                          │         │
│       └────────────────────────│  1. Recibe ticket        │         │
│                                │  2. Busca equipo         │         │
│                                │  3. Crea mantenimiento   │         │
│                                │  4. Notifica técnico     │         │
│                                └──────────┬───────────────┘         │
│                                           │                         │
│                        ┌──────────────────┼──────────────┐          │
│                        ▼                  ▼              ▼          │
│                   ┌─────────┐     ┌──────────┐   ┌──────────┐      │
│                   │ Web App │     │ App Móvil │   │ Firebase │      │
│                   │ CRESIO  │     │  CRESIO   │   │   Push   │      │
│                   └─────────┘     └──────────┘   └──────────┘      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 Modelo de comunicación

| Dirección | Protocolo | Mecanismo | Propósito |
|-----------|-----------|-----------|-----------|
| Odoo → CRESIO | **HTTP POST** (webhook) | Automated Action en Odoo | Enviar ticket nuevo/actualizado |
| CRESIO → Odoo | **XML-RPC** | `execute_kw` en `/xmlrpc/2/object` | Actualizar estado del ticket |

---

## 4. IMPLEMENTACIÓN DETALLADA

### FASE 1: Receptor de Webhooks en CRESIO (Backend)

**Esfuerzo estimado:** 2-3 días  
**Prioridad:** ALTA

#### 4.1.1 Nuevo DTO para webhook de Odoo

```java
// presentacion/dto/request/OdooTicketWebhookDTO.java
public class OdooTicketWebhookDTO {
    
    private Long odooTicketId;          // ID del ticket en Odoo
    private String nombre;              // Asunto del ticket
    private String descripcion;         // Descripción detallada
    private String prioridad;           // "0"=baja, "1"=media, "2"=alta, "3"=urgente
    private String equipoSerial;        // Serial del equipo afectado
    private String equipoCodigoSap;     // Código SAP/inventario del equipo
    private String contactoNombre;      // Nombre del usuario que reporta
    private String contactoEmail;       // Email del reportante
    private String etapa;               // Nombre de la etapa en Odoo
    private String equipo;              // Equipo de soporte asignado en Odoo
    private String webhookSecret;       // Token secreto para validar autenticidad
    
    // Getters y setters...
}
```

#### 4.1.2 Nuevo controlador para webhook

```java
// presentacion/controladores/OdooTicketControlador.java

@RestController
@RequestMapping("/api/tickets")
public class OdooTicketControlador {

    private final IOdooIntegracionServicio odooServicio;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> recibirTicket(
            @RequestBody OdooTicketWebhookDTO webhook) {
        
        // 1. Validar token secreto
        // 2. Buscar equipo por serial o codigoSap
        // 3. Crear mantenimiento con tipoOrigen = ODOO_HELPDESK
        // 4. Enviar notificación push al técnico asignado
        // 5. Retornar confirmación con ID del mantenimiento creado
        
        var resultado = odooServicio.procesarTicket(webhook);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/webhook/actualizar")
    public ResponseEntity<Map<String, Object>> actualizarTicket(
            @RequestBody OdooTicketWebhookDTO webhook) {
        
        // Actualizar estado del mantenimiento si el ticket cambia en Odoo
        var resultado = odooServicio.actualizarDesdeOdoo(webhook);
        return ResponseEntity.ok(resultado);
    }
}
```

#### 4.1.3 Servicio de integración Odoo

```java
// aplicacion/casosuso/impl/OdooIntegracionServicioImpl.java

@Service
public class OdooIntegracionServicioImpl implements IOdooIntegracionServicio {

    private final IMantenimientosUseCase mantenimientosUseCase;
    private final IEquiposUseCase equiposUseCase;
    private final PushNotificacionService pushService;
    
    @Value("${odoo.webhook.secret}")
    private String webhookSecret;

    @Override
    @Transactional
    public Map<String, Object> procesarTicket(OdooTicketWebhookDTO webhook) {
        
        // 1. VALIDAR AUTENTICIDAD
        if (!webhookSecret.equals(webhook.getWebhookSecret())) {
            throw new SecurityException("Token de webhook inválido");
        }
        
        // 2. BUSCAR EQUIPO EN CRESIO
        Equipos equipo = buscarEquipo(
            webhook.getEquipoSerial(), 
            webhook.getEquipoCodigoSap()
        );
        
        // 3. CREAR MANTENIMIENTO
        Mantenimientos mantenimiento = new Mantenimientos();
        mantenimiento.setEquipoId(equipo.getIdEquipo());
        mantenimiento.setDescripcion(
            "[Odoo #" + webhook.getOdooTicketId() + "] " + 
            webhook.getNombre() + "\n\n" + webhook.getDescripcion()
        );
        mantenimiento.setTipoOrigen(TipoOrigenMantenimiento.ODOO_HELPDESK);
        mantenimiento.setOdooTicketId(String.valueOf(webhook.getOdooTicketId()));
        mantenimiento.setEstado("PENDIENTE");
        mantenimiento.setEstadoInterno(EstadoInternoMantenimiento.PENDIENTE);
        mantenimiento.setTipoMantenimiento("CORRECTIVO");
        
        Mantenimientos creado = mantenimientosUseCase.crear(mantenimiento);
        
        // 4. NOTIFICAR TÉCNICOS
        pushService.enviarATecnicos(
            "Nuevo ticket Odoo #" + webhook.getOdooTicketId(),
            "Equipo: " + equipo.getCodigoSap() + " - " + webhook.getNombre()
        );
        
        return Map.of(
            "status", "ok",
            "mantenimientoId", creado.getIdMantenimiento(),
            "mensaje", "Mantenimiento creado exitosamente desde ticket Odoo"
        );
    }
    
    private Equipos buscarEquipo(String serial, String codigoSap) {
        // Buscar primero por codigoSap, luego por serial
        if (codigoSap != null && !codigoSap.isBlank()) {
            return equiposUseCase.buscarPorCodigoSap(codigoSap);
        }
        if (serial != null && !serial.isBlank()) {
            return equiposUseCase.buscarPorSerial(serial);
        }
        throw new IllegalArgumentException(
            "Se requiere serial o codigoSap del equipo para vincular el ticket"
        );
    }
}
```

#### 4.1.4 Configuración de propiedades

```properties
# application.properties
odoo.webhook.secret=${ODOO_WEBHOOK_SECRET:cresio-odoo-secret-2025}
odoo.url=${ODOO_URL:http://localhost:8069}
odoo.db=${ODOO_DB:odoo17}
odoo.username=${ODOO_USERNAME:admin}
odoo.password=${ODOO_PASSWORD:admin}
```

#### 4.1.5 Actualización de SecurityConfig

```java
// Cambiar la línea existente:
.requestMatchers("/api/tickets/**").hasAnyRole("ADMINISTRADOR", "TECNICO")

// Por:
.requestMatchers("/api/tickets/webhook/**").permitAll()  // Webhook usa su propio token
.requestMatchers("/api/tickets/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
```

> **Nota de seguridad:** El endpoint webhook usa `permitAll()` porque Odoo no envía credenciales Basic Auth. La autenticación se valida mediante el `webhookSecret` incluido en el body del request.

---

### FASE 2: Configuración de Odoo (Automated Action / Webhook)

**Esfuerzo estimado:** 1 día  
**Prioridad:** ALTA  
**Requiere:** Acceso de administrador a Odoo v17

#### 4.2.1 Crear campo personalizado en Odoo (opcional pero recomendado)

En Odoo, ir a **Configuración → Técnico → Campos** y agregar al modelo `helpdesk.ticket`:

| Campo | Nombre técnico | Tipo | Descripción |
|-------|---------------|------|-------------|
| Código SAP Equipo | `x_codigo_sap_equipo` | Char | Código de inventario del equipo en CRESIO |
| Serial Equipo | `x_serial_equipo` | Char | Número de serie del equipo |
| ID Mantenimiento CRESIO | `x_cresio_mantenimiento_id` | Integer | ID del mantenimiento creado en CRESIO |

> **Alternativa sin Studio:** Estos campos se pueden crear vía XML-RPC usando la API de `ir.model.fields`:
> ```python
> # Crear campo personalizado en helpdesk.ticket
> models.execute_kw(db, uid, password, 'ir.model.fields', 'create', [{
>     'model_id': model_id,  # ID del modelo helpdesk.ticket
>     'name': 'x_codigo_sap_equipo',
>     'ttype': 'char',
>     'field_description': 'Código SAP Equipo (CRESIO)',
>     'state': 'manual',
> }])
> ```

#### 4.2.2 Configurar Automated Action en Odoo

Ir a **Configuración → Técnico → Acciones automatizadas** y crear:

| Parámetro | Valor |
|-----------|-------|
| **Nombre** | Enviar ticket a CRESIO |
| **Modelo** | Ticket de Mesa de Ayuda (helpdesk.ticket) |
| **Disparador** | Al crear / Al escribir |
| **Condición (dominio)** | `[('x_codigo_sap_equipo', '!=', False)]` |
| **Acción** | Ejecutar código Python |

#### 4.2.3 Código Python de la Automated Action

```python
# Código a colocar en la Automated Action de Odoo v17
import json
import requests

for ticket in records:
    # Solo procesar si tiene código SAP o serial
    if not ticket.x_codigo_sap_equipo and not ticket.x_serial_equipo:
        continue
    
    # Construir payload
    payload = {
        "odooTicketId": ticket.id,
        "nombre": ticket.name or "",
        "descripcion": ticket.description or "",
        "prioridad": ticket.priority or "0",
        "equipoCodigoSap": ticket.x_codigo_sap_equipo or "",
        "equipoSerial": ticket.x_serial_equipo or "",
        "contactoNombre": ticket.partner_id.name if ticket.partner_id else "",
        "contactoEmail": ticket.partner_id.email if ticket.partner_id else "",
        "etapa": ticket.stage_id.name if ticket.stage_id else "",
        "equipo": ticket.team_id.name if ticket.team_id else "",
        "webhookSecret": "cresio-odoo-secret-2025"
    }
    
    # Enviar a CRESIO
    try:
        url = "http://IP_SERVIDOR_CRESIO:8083/api/tickets/webhook"
        headers = {"Content-Type": "application/json"}
        response = requests.post(url, json=payload, headers=headers, timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            # Guardar ID del mantenimiento CRESIO en el ticket de Odoo
            ticket.write({
                'x_cresio_mantenimiento_id': data.get('mantenimientoId', 0)
            })
            # Registrar en el chatter del ticket
            ticket.message_post(
                body=f"✅ Mantenimiento CRESIO #{data.get('mantenimientoId')} creado automáticamente.",
                message_type='notification'
            )
        else:
            ticket.message_post(
                body=f"⚠️ Error al enviar a CRESIO: {response.status_code} - {response.text}",
                message_type='notification'
            )
    except Exception as e:
        ticket.message_post(
            body=f"❌ Error de conexión con CRESIO: {str(e)}",
            message_type='notification'
        )
```

> **IMPORTANTE:** Reemplazar `IP_SERVIDOR_CRESIO` con la IP real del servidor donde corre la API de CRESIO.

#### 4.2.4 Flujo del usuario en Odoo (operador de mesa de ayuda)

```
1. Usuario llama/escribe reportando fallo en equipo
2. Operador crea ticket en Odoo Helpdesk
3. Operador llena el campo "Código SAP Equipo" (ej: "SAP-001234")
   → Lo obtiene preguntando al usuario o buscando en el inventario
4. Al guardar el ticket:
   → Odoo ejecuta la Automated Action automáticamente
   → CRESIO recibe el webhook
   → Se crea un mantenimiento CORRECTIVO vinculado al equipo
   → El técnico recibe notificación push en su celular
5. En el chatter del ticket de Odoo aparece:
   "✅ Mantenimiento CRESIO #157 creado automáticamente"
```

---

### FASE 3: Sincronización Bidireccional (CRESIO → Odoo)

**Esfuerzo estimado:** 2-3 días  
**Prioridad:** MEDIA

#### 4.3.1 Cliente XML-RPC para Odoo

```java
// infraestructura/odoo/OdooXmlRpcClient.java

@Service
public class OdooXmlRpcClient {

    @Value("${odoo.url}")
    private String odooUrl;
    
    @Value("${odoo.db}")
    private String odooDb;
    
    @Value("${odoo.username}")
    private String odooUsername;
    
    @Value("${odoo.password}")
    private String odooPassword;
    
    private Integer uid;

    /**
     * Autenticar con Odoo y obtener UID
     */
    public Integer autenticar() throws Exception {
        XmlRpcClient client = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(odooUrl + "/xmlrpc/2/common"));
        client.setConfig(config);
        
        this.uid = (Integer) client.execute("authenticate", 
            new Object[]{odooDb, odooUsername, odooPassword, new HashMap<>()});
        return this.uid;
    }

    /**
     * Actualizar el estado de un ticket en Odoo
     */
    public void actualizarEstadoTicket(Long odooTicketId, String nuevoEstado) 
            throws Exception {
        if (uid == null) autenticar();
        
        XmlRpcClient models = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(odooUrl + "/xmlrpc/2/object"));
        models.setConfig(config);
        
        // Buscar etapa de Odoo por nombre
        Object[] stageIds = (Object[]) models.execute("execute_kw",
            new Object[]{odooDb, uid, odooPassword, 
                "helpdesk.stage", "search",
                new Object[]{new Object[]{
                    new Object[]{"name", "=", nuevoEstado}
                }}
            });
        
        if (stageIds.length > 0) {
            // Mover el ticket a la nueva etapa
            models.execute("execute_kw",
                new Object[]{odooDb, uid, odooPassword,
                    "helpdesk.ticket", "write",
                    new Object[]{
                        new Object[]{odooTicketId.intValue()},  // IDs
                        new HashMap<String, Object>() {{
                            put("stage_id", stageIds[0]);
                        }}
                    }
                });
        }
    }

    /**
     * Agregar nota interna al ticket de Odoo
     */
    public void agregarNota(Long odooTicketId, String mensaje) throws Exception {
        if (uid == null) autenticar();
        
        XmlRpcClient models = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(odooUrl + "/xmlrpc/2/object"));
        models.setConfig(config);
        
        models.execute("execute_kw",
            new Object[]{odooDb, uid, odooPassword,
                "helpdesk.ticket", "message_post",
                new Object[]{new Object[]{odooTicketId.intValue()}},
                new HashMap<String, Object>() {{
                    put("body", mensaje);
                    put("message_type", "comment");
                    put("subtype_xmlid", "mail.mt_note");
                }}
            });
    }
}
```

#### 4.3.2 Eventos que disparan sincronización CRESIO → Odoo

| Evento en CRESIO | Acción en Odoo |
|-------------------|----------------|
| Técnico inicia mantenimiento | Mover ticket a etapa "En Proceso" + nota |
| Técnico completa mantenimiento | Mover ticket a etapa "Resuelto" + nota con resumen |
| Se sube evidencia fotográfica | Nota: "Se adjuntaron N fotos al mantenimiento" |
| Se genera acta firmada | Nota: "Acta de mantenimiento firmada y disponible" |
| Mantenimiento cancelado | Mover ticket a etapa reabierto + nota con motivo |

#### 4.3.3 Mapeo de estados

| Estado CRESIO (`estadoInterno`) | Etapa Odoo (sugerida) |
|-------------------------------|---------------------|
| `PENDIENTE` | En Proceso |
| `EN_PROCESO` | En Proceso |
| `COMPLETADO` | Resuelto |
| `CANCELADO` | Cancelado |

---

### FASE 4: Visualización en CRESIO (Frontend Web)

**Esfuerzo estimado:** 1-2 días  
**Prioridad:** MEDIA

#### 4.4.1 Indicador de origen Odoo en la vista de mantenimientos

En la tabla de mantenimientos, agregar una columna o badge que muestre:

```html
<!-- Badge de origen -->
<span th:if="${mantenimiento.tipoOrigen == 'ODOO_HELPDESK'}" 
      class="badge bg-purple">
    <i class="fas fa-ticket-alt"></i> Odoo #[[${mantenimiento.odooTicketId}]]
</span>
<span th:if="${mantenimiento.tipoOrigen == 'PROGRAMADO'}" 
      class="badge bg-info">
    <i class="fas fa-calendar"></i> Programado
</span>
<span th:if="${mantenimiento.tipoOrigen == 'MANUAL'}" 
      class="badge bg-secondary">
    <i class="fas fa-hand-paper"></i> Manual
</span>
```

#### 4.4.2 Link directo a Odoo desde CRESIO

```html
<!-- En el detalle del mantenimiento -->
<a th:if="${mantenimiento.odooTicketId != null}" 
   th:href="@{${odooUrl} + '/web#id=' + ${mantenimiento.odooTicketId} + '&model=helpdesk.ticket&view_type=form'}"
   target="_blank" class="btn btn-sm btn-outline-purple">
    <i class="fas fa-external-link-alt"></i> Ver ticket en Odoo
</a>
```

---

### FASE 5: Soporte en App Móvil (Flutter)

**Esfuerzo estimado:** 1 día  
**Prioridad:** BAJA (la app ya muestra mantenimientos independientemente del origen)

La app móvil CRESIO ya muestra todos los mantenimientos asignados al técnico. Los mantenimientos creados desde Odoo aparecerán automáticamente, ya que comparten el mismo modelo de datos. Solo se necesita:

- Mostrar el badge "Odoo Ticket #xxx" en la tarjeta del mantenimiento
- Incluir en la descripción el contexto del ticket original

---

## 5. CONFIGURACIÓN PASO A PASO

### 5.1 En el servidor CRESIO (Backend Java)

```
Paso 1: Agregar dependencia Apache XML-RPC al pom.xml
        → org.apache.xmlrpc:xmlrpc-client:3.1.3

Paso 2: Crear los archivos:
        → OdooTicketWebhookDTO.java
        → OdooTicketControlador.java
        → IOdooIntegracionServicio.java (interfaz)
        → OdooIntegracionServicioImpl.java
        → OdooXmlRpcClient.java

Paso 3: Actualizar SecurityConfig.java
        → Permitir /api/tickets/webhook/** sin autenticación

Paso 4: Agregar propiedades de Odoo en application.properties

Paso 5: Agregar métodos de búsqueda en repositorio de equipos:
        → buscarPorCodigoSap(String codigoSap)
        → buscarPorSerial(String serial)

Paso 6: Compilar y desplegar
        → ./mvnw clean package -DskipTests
```

### 5.2 En Odoo v17

```
Paso 1: Activar modo desarrollador
        → Configuración → Activar modo desarrollador

Paso 2: Crear campos personalizados en helpdesk.ticket
        → x_codigo_sap_equipo (Char)
        → x_serial_equipo (Char)
        → x_cresio_mantenimiento_id (Integer)

Paso 3: Agregar campos al formulario del ticket
        → Configuración → Técnico → Vistas
        → Modificar vista form de helpdesk.ticket

Paso 4: Crear Automated Action
        → Configuración → Técnico → Acciones automatizadas
        → Modelo: helpdesk.ticket
        → Disparador: Al crear
        → Condición: x_codigo_sap_equipo no está vacío
        → Acción: Ejecutar código Python (ver sección 4.2.3)

Paso 5: Probar
        → Crear ticket de prueba con código SAP de un equipo existente en CRESIO
        → Verificar que el mantenimiento se cree automáticamente
```

---

## 6. DEPENDENCIA MAVEN REQUERIDA

```xml
<!-- pom.xml de gestionactivosapi -->
<dependency>
    <groupId>org.apache.xmlrpc</groupId>
    <artifactId>xmlrpc-client</artifactId>
    <version>3.1.3</version>
</dependency>
```

---

## 7. SEGURIDAD DE LA INTEGRACIÓN

### 7.1 Autenticación del webhook

| Mecanismo | Descripción |
|-----------|-------------|
| **Token secreto** | Cada request de Odoo incluye un `webhookSecret` que CRESIO valida |
| **HTTPS** | En producción, OBLIGATORIO usar HTTPS para cifrar el tráfico |
| **IP whitelist** (opcional) | Filtrar requests solo desde la IP del servidor Odoo |
| **Rate limiting** (recomendado) | Limitar a 60 requests/minuto para evitar abusos |

### 7.2 Credenciales de Odoo en CRESIO

| Práctica | Implementación |
|----------|---------------|
| No hardcodear credenciales | Usar variables de entorno: `ODOO_URL`, `ODOO_USERNAME`, `ODOO_PASSWORD` |
| API Key en lugar de password | Crear API Key en Odoo → Preferencias → Seguridad de Cuenta |
| Principio de mínimo privilegio | Crear un usuario "cresio-integration" en Odoo con permisos solo en Helpdesk |

### 7.3 Validación de datos

```java
// Toda entrada del webhook debe ser validada:
// - odooTicketId: numérico positivo
// - equipoCodigoSap/serial: sanitizar contra inyección SQL (JPA lo maneja)
// - descripcion: limitar longitud a 5000 caracteres
// - webhookSecret: comparación constante-time para evitar timing attacks
```

---

## 8. ESCENARIOS DE USO

### Escenario 1: Pantalla rota de laptop

```
1. Empleado llama a mesa de ayuda: "Se me cayó la laptop y la pantalla está rota"
2. Operador Odoo crea ticket:
   - Asunto: "Pantalla rota - Laptop HP"
   - Código SAP Equipo: "SAP-002156" (lo busca en inventario)
   - Prioridad: Alta
3. → Odoo envía webhook a CRESIO automáticamente
4. → CRESIO crea mantenimiento CORRECTIVO vinculado al equipo SAP-002156
5. → Técnico Roberto Zambrano recibe push notification en su celular
6. → Técnico va al sitio, toma fotos de la pantalla rota
7. → Registra diagnóstico y repuestos usados en la app móvil
8. → Completa el mantenimiento con firma digital del empleado
9. → CRESIO actualiza el ticket en Odoo: etapa "Resuelto"
10. → En Odoo aparece nota: "Mantenimiento CRESIO #157 completado"
```

### Escenario 2: Equipo lento (diagnóstico)

```
1. Empleado reporta vía portal: "Mi computadora está muy lenta"
2. Ticket creado en Odoo con código SAP del equipo
3. → Webhook a CRESIO → mantenimiento creado
4. Técnico revisa el checklist en la app:
   - ✅ Verificar espacio en disco
   - ✅ Verificar RAM disponible
   - ✅ Escanear malware
   - ✅ Verificar actualizaciones
5. Resultado: RAM insuficiente (4GB), se recomienda upgrade
6. → CRESIO actualiza ticket Odoo con el diagnóstico completo
7. → Operador Odoo gestiona la compra del repuesto
```

### Escenario 3: Auditoría de mantenimientos

```
1. Auditor ingresa a CRESIO Web
2. Filtra mantenimientos por origen: "ODOO_HELPDESK"
3. Ve listado de todos los mantenimientos generados desde tickets
4. Cada registro tiene:
   - Link al ticket original en Odoo
   - Evidencia fotográfica
   - Acta firmada en PDF
   - Historial completo del equipo
5. Genera reporte Excel de mantenimientos del mes
```

---

## 9. CRONOGRAMA DE IMPLEMENTACIÓN

| Fase | Tarea | Esfuerzo | Dependencia |
|------|-------|----------|-------------|
| **1** | Crear endpoint webhook en CRESIO | 2-3 días | Ninguna |
| **1** | Servicio de integración + búsqueda de equipos | 1 día | Fase 1.1 |
| **1** | Tests unitarios del webhook | 1 día | Fase 1.2 |
| **2** | Campos personalizados en Odoo | 0.5 días | Ninguna |
| **2** | Automated Action + código Python | 0.5 días | Fase 2.1 |
| **2** | Pruebas de integración Odoo → CRESIO | 1 día | Fases 1 y 2 |
| **3** | Cliente XML-RPC para Odoo | 2 días | Ninguna |
| **3** | Eventos de sync CRESIO → Odoo | 1 día | Fase 3.1 |
| **4** | Badges y links en frontend web | 1 día | Fase 1 |
| **5** | Badge en app móvil | 0.5 días | Fase 1 |
| | **TOTAL ESTIMADO** | **~10-12 días** | |

### Ejecución recomendada por sprints

| Sprint | Fases | Resultado |
|--------|-------|-----------|
| **Sprint 1** (1 semana) | Fase 1 + 2 | Tickets de Odoo crean mantenimientos en CRESIO automáticamente |
| **Sprint 2** (1 semana) | Fase 3 + 4 + 5 | Sincronización bidireccional + visualización completa |

---

## 10. REQUISITOS PREVIOS

### De infraestructura

- [ ] Servidor CRESIO accesible desde servidor Odoo (red/firewall)
- [ ] HTTPS configurado en el servidor CRESIO (certificado SSL)
- [ ] Variables de entorno configuradas en ambos servidores

### De Odoo

- [ ] Módulo Helpdesk instalado y operativo
- [ ] Acceso de administrador para crear campos y Automated Actions
- [ ] Módulo Python `requests` disponible en el servidor Odoo
- [ ] Plan de Odoo que permita acceso a API externa (Custom, no One App Free)

### De CRESIO

- [ ] Equipos cargados en el sistema con `codigoSap` o `serial` únicos
- [ ] Al menos un técnico activo con FCM token registrado
- [ ] API backend corriendo y accesible

---

## 11. ALTERNATIVA: POLLING EN LUGAR DE WEBHOOK

Si la infraestructura de red no permite que Odoo envíe webhooks a CRESIO (ej: firewalls restrictivos, Odoo en la nube y CRESIO on-premise), se puede implementar un **modelo de polling**:

```
CRESIO (cada 5 minutos) → Consulta Odoo XML-RPC → ¿Hay tickets nuevos?
                ↓ SÍ
        Crear mantenimiento localmente
```

```java
// Tarea programada con @Scheduled
@Scheduled(fixedRate = 300000) // cada 5 minutos
public void sincronizarTicketsOdoo() {
    // 1. Consultar tickets nuevos en Odoo con x_codigo_sap_equipo != false
    // 2. Filtrar los que no tienen x_cresio_mantenimiento_id
    // 3. Para cada ticket: crear mantenimiento en CRESIO
    // 4. Actualizar x_cresio_mantenimiento_id en Odoo
}
```

**Ventajas del polling:** No requiere que CRESIO sea accesible públicamente.  
**Desventajas:** Delay de hasta 5 minutos. Mayor consumo de recursos.

---

## 12. RESUMEN Y RECOMENDACIONES

### Lo que YA está listo (0 esfuerzo adicional)

✅ Campo `odoo_ticket_id` en la BD  
✅ Enum `ODOO_HELPDESK` como tipo de origen  
✅ Validación de negocio para tickets Odoo  
✅ DTOs con soporte para `odooTicketId` y `tipoOrigen`  
✅ Mappers MapStruct configurados  
✅ Ruta `/api/tickets/**` autorizada en SecurityConfig  
✅ Firebase FCM operativo para notificaciones push  

### Lo que se necesita construir

🔧 Controlador webhook (`OdooTicketControlador`)  
🔧 Servicio de integración (`OdooIntegracionServicioImpl`)  
🔧 Búsqueda de equipos por código SAP/serial  
🔧 Cliente XML-RPC para sincronización inversa  
🔧 Configuración de Automated Action en Odoo  
🔧 Campos personalizados en formulario de ticket de Odoo  
🔧 Badges visuales en frontend web y app móvil  

### Recomendación final

Iniciar con las **Fases 1 y 2** (webhook unidireccional Odoo → CRESIO). Esto da resultados inmediatos con mínimo esfuerzo: los tickets de soporte técnico se canalizarán automáticamente al sistema de gestión de activos, donde los técnicos pueden documentar todo el proceso con fotos, firmas y checklists. La Fase 3 (bidireccional) se puede agregar después para cerrar el ciclo completo.

---

*Fin del informe.*
