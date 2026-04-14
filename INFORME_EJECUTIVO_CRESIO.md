# INFORME EJECUTIVO — Sistema CRESIO
## Sistema de Gestión Integral de Activos Tecnológicos

**Fecha:** Abril 2026  
**Dirigido a:** Líder de Proyecto / Gerencia  
**Elaborado por:** Equipo de Desarrollo  

---

## 1. RESUMEN EJECUTIVO

**CRESIO** es un sistema integral de gestión de activos tecnológicos compuesto por **tres plataformas interconectadas** que automatizan y digitalizan todo el ciclo de vida de los equipos de TI en la empresa: desde su registro e inventario, pasando por la asignación a custodios, el mantenimiento preventivo y correctivo, hasta la baja del activo.

| Componente | Tecnología | Propósito |
|---|---|---|
| **API Backend** | Java 17 + Spring Boot + PostgreSQL | Motor de negocio, seguridad y datos |
| **Portal Web** | Spring MVC + Thymeleaf + Bootstrap | Administración y reportes para oficina |
| **App Móvil** | Flutter (Android) | Trabajo de campo para técnicos |

---

## 2. PROBLEMÁTICA QUE RESUELVE

### Sin CRESIO (Situación actual)
| Problema | Impacto |
|---|---|
| Inventario de equipos en hojas de cálculo | Datos desactualizados, pérdida de activos, duplicados |
| Actas de custodia manuales en papel | Sin trazabilidad, difícil auditoría, documentos extraviados |
| Mantenimientos sin planificar | Equipos deteriorados, costos reactivos elevados |
| Sin control de ubicación de técnicos | Imposible verificar cobertura de servicio |
| Reportes manuales | Horas de trabajo para consolidar información |
| Sin alertas ni notificaciones | Mantenimientos vencidos sin atención |

### Con CRESIO (Solución)
| Solución | Beneficio |
|---|---|
| Inventario digital centralizado con código QR | Datos en tiempo real, búsqueda instantánea, cero duplicados |
| Actas digitales con firma electrónica y PDF | 100% trazabilidad, envío automático por correo |
| Planificación y checklist de mantenimientos | Mantenimiento preventivo, reducción de costos |
| GPS en tiempo real de técnicos | Supervisión y optimización de rutas |
| Reportes automáticos con gráficos y exportación Excel/PDF | Decisiones basadas en datos, ahorro de tiempo |
| Notificaciones push y alertas | Atención inmediata a eventos críticos |

---

## 3. MÓDULOS FUNCIONALES

### 3.1 📦 Gestión de Equipos (Inventario)
- **Registro completo** de activos: marca, modelo, serie, MAC, estado, categoría, ubicación, custodio
- **Estados de equipo**: Funcional, Parcialmente Funcional, Deteriorado, No Funcional
- **Historial de cambios**: cada modificación queda registrada con fecha y usuario
- **Importación masiva**: carga de equipos desde archivos CSV/JSON
- **Escaneo QR/códigos de barras** desde la app móvil para identificación rápida
- **Vistas de inventario**: por sucursal, por departamento, por custodio
- **Exportación a Excel** del inventario completo

### 3.2 🔧 Mantenimientos
- **Tipos**: Preventivo, Correctivo, Predictivo
- **Orígenes**: Planeado, No Planeado, SOS (urgencia)
- **Prioridades**: Alta, Media, Baja
- **Mantenimiento multi-equipo**: una sola orden puede cubrir varios equipos
- **Checklist de actividades** personalizable por categoría de equipo
- **Captura de evidencia fotográfica** (múltiples fotos por mantenimiento)
- **Firma digital** del técnico y del custodio en pantalla táctil
- **Generación automática de PDF** del informe técnico
- **Envío automático por correo** del reporte al cerrar la orden
- **Historial completo** de mantenimientos por equipo
- **Estados de seguimiento**: Creado → En Proceso → Completado → Entregado

### 3.3 📅 Mantenimiento Programado (Preventivo)
- Definición de **frecuencias** por categoría: semanal, mensual, trimestral, semestral, anual
- **Alertas automáticas** de mantenimientos vencidos o próximos a vencer
- **Recálculo automático** de fechas al cerrar una orden
- Reducción significativa de **mantenimientos correctivos costosos**

### 3.4 📅 Planificación de Actividades
- **Tipos de tarea**: Diaria, Semanal, Mantenimiento Programado, Visita Técnica, Objetivo Mensual
- **Prioridades**: Baja, Media, Alta, Urgente
- **Métricas de cumplimiento**: por técnico y global
- **Indicadores**: completadas, pendientes, vencidas, en progreso, % de cumplimiento
- **Filtros por período**: mensual, trimestral, anual
- Visibilidad total del rendimiento del equipo técnico

### 3.5 📋 Custodias y Actas Legales
- **Asignación formal** de equipos a custodios (empleados responsables)
- **Tipos de acta**: Asignación, Traslado, Baja
- **Generación automática de actas PDF** desde plantillas corporativas (.docx)
- **Firma digital** de actas
- **Almacenamiento seguro** de actas firmadas
- **Reenvío por correo electrónico** de actas
- **Trazabilidad completa**: quién tiene qué equipo, desde cuándo, con qué documento
- **Exportación a Excel** del reporte de custodias

### 3.6 🗺️ Visitas Técnicas
- **Vista por ubicación**: qué equipos hay en cada sucursal/sede
- **Vista por custodio**: qué equipos tiene asignados cada persona
- **Último mantenimiento**: días transcurridos desde el último servicio
- Planificación eficiente de visitas de campo

### 3.7 📍 GPS y Monitoreo de Técnicos
- **Consentimiento informado** del técnico antes de activar el rastreo
- **Envío automático de coordenadas** cada 30 segundos desde la app móvil
- **Mapa en tiempo real** con la ubicación de todos los técnicos activos (vista administrador)
- **Historial de rutas** por técnico con filtro de fechas
- Optimización de asignación de visitas por proximidad geográfica

### 3.8 🔔 Notificaciones y Alertas
- **Push notifications** en la app móvil vía Firebase Cloud Messaging
- **Centro de notificaciones** en web y móvil
- **Alertas de mantenimientos** vencidos o próximos
- **Notificaciones leídas/no leídas** con contadores
- Atención inmediata a eventos críticos

### 3.9 📊 Reportes y Centro de Datos
- **Dashboard ejecutivo** con KPIs en tiempo real:
  - Total de equipos y equipos activos
  - Total de mantenimientos y mantenimientos abiertos
  - Total de custodias y custodios
- **Gráficos interactivos**:
  - Equipos por categoría (pie)
  - Equipos por marca — top 10 (barras)
  - Equipos por estado (pie)
  - Mantenimientos por tipo (pie)
  - Mantenimientos por estado interno (pie)
  - Mantenimientos por técnico — top 10 (barras)
  - Tendencia de mantenimientos últimos 6 meses (línea)
- **Reportes exportables a Excel**: equipos, mantenimientos, custodias
- **Reportes exportables a PDF**: actas, informes de mantenimiento
- **Métricas de cumplimiento** de actividades planificadas por técnico

### 3.10 👥 Administración del Sistema
- **Gestión de usuarios** con roles diferenciados
- **Gestión de ubicaciones/sucursales** con coordenadas GPS
- **Gestión de departamentos, cargos, marcas, categorías de equipo**
- **Checklist de actividades** configurables por categoría
- **Roles y permisos** granulares por módulo
- **Setup inicial** automatizado (creación de admin inicial)

---

## 4. SEGURIDAD

| Característica | Descripción |
|---|---|
| **Autenticación** | HTTP Basic + JWT con cifrado BCrypt |
| **Roles** | ADMINISTRADOR, TÉCNICO, AUDITOR |
| **Permisos granulares** | Control por endpoint, método HTTP y módulo |
| **Sesiones seguras** | JWT con expiración, almacenamiento seguro en móvil |
| **Auditoría** | Registro de cambios en base de datos |
| **Consentimiento GPS** | Técnicos deben aceptar antes del rastreo |
| **Validación de datos** | Cédula ecuatoriana, correos, RUC, campos obligatorios |
| **Perfilamiento por rol** | Cada usuario solo ve los módulos autorizados |

### Matriz de Permisos por Rol

| Módulo | Administrador | Técnico | Auditor |
|---|:---:|:---:|:---:|
| Gestión de equipos (CRUD) | ✅ | ✅ | 🔍 Solo lectura |
| Mantenimientos | ✅ | ✅ | 🔍 Solo lectura |
| Custodias y actas | ✅ | ✅ | ❌ |
| Planificación | ✅ | ✅ | ❌ |
| GPS tiempo real | ✅ (ver mapa) | 📍 (enviar ubicación) | ❌ |
| Reportes | ✅ | ✅ | ✅ |
| Usuarios y roles | ✅ | ❌ | ❌ |
| Departamentos/Ubicaciones | ✅ | ❌ | ❌ |
| Importación masiva | ✅ | ❌ | ❌ |

---

## 5. CAPACIDAD OFFLINE (Aplicación Móvil)

Una de las características más valiosas del sistema es su **capacidad de trabajo sin conexión a internet**, crítica para técnicos que trabajan en campo.

| Funcionalidad | Comportamiento Offline |
|---|---|
| Crear mantenimiento | Se guarda localmente en SQLite y se sincroniza al reconectar |
| Cerrar mantenimiento | Se encola y se envía cuando hay conexión |
| Fotos de evidencia | Se almacenan en el dispositivo y se suben después |
| Firmas digitales | Se guardan como base64 y se envían en la sincronización |
| Catálogos (equipos, custodios) | Cache local para consulta sin conexión |

**Indicador visual** en el dashboard: el técnico siempre sabe cuántas operaciones tiene pendientes de sincronizar.

---

## 6. ARQUITECTURA TÉCNICA

```
┌─────────────────────────────────────────────────────────────────┐
│                     USUARIOS FINALES                            │
├─────────────────────┬───────────────────────────────────────────┤
│   🖥️ Portal Web     │          📱 App Móvil (Android)           │
│   (Administración)  │          (Técnicos en campo)              │
│   Spring MVC +      │          Flutter + SQLite offline         │
│   Thymeleaf         │          GPS + Cámara + Firma             │
│   Puerto: 8081      │          QR Scanner                       │
├─────────────────────┴───────────────────────────────────────────┤
│                    🔗 REST API (JSON)                           │
├─────────────────────────────────────────────────────────────────┤
│                 ⚙️ API Backend (Java 17)                        │
│          Spring Boot + Spring Security + JWT                    │
│          Arquitectura Hexagonal (Clean Architecture)            │
│          Puerto: 8083                                           │
├─────────────────────────────────────────────────────────────────┤
│  🗄️ PostgreSQL  │  📧 SMTP Gmail  │  🔥 Firebase FCM  │  📁 FS │
│  Base de datos   │  Envío correos  │  Push notif.       │ PDFs   │
└─────────────────────────────────────────────────────────────────┘
```

### Stack Tecnológico

| Capa | Tecnología | Versión |
|---|---|---|
| Backend API | Java + Spring Boot | 17 / 4.0.2 |
| Base de datos | PostgreSQL + Flyway | 15+ |
| Frontend Web | Thymeleaf + Bootstrap | - |
| App Móvil | Flutter + Dart | 3.x |
| Mapeo objetos | MapStruct | 1.6.3 |
| Reportes PDF | OpenPDF | 1.3.39 |
| Reportes Excel | Apache POI | 5.2.5 |
| Push Notifications | Firebase Cloud Messaging | - |
| Contenedores | Docker + Docker Compose | - |

---

## 7. BENEFICIOS ESPERADOS EN PRODUCCIÓN

### 7.1 Beneficios Operativos

| Beneficio | Descripción | Impacto estimado |
|---|---|---|
| **Eliminación del papel** | Actas, reportes y checklists 100% digitales | Ahorro en materiales y almacenamiento físico |
| **Reducción de tiempos** | Generación automática de reportes y PDFs | De horas a segundos |
| **Mantenimiento preventivo** | Alertas automáticas antes del vencimiento | Reducción de fallos y costos de reparación |
| **Trazabilidad total** | Historial completo de cada activo | Auditorías sin esfuerzo |
| **Productividad de técnicos** | App móvil con trabajo offline | Sin pérdida de datos en campo |
| **Supervisión en tiempo real** | GPS de técnicos + dashboard de KPIs | Mejor asignación de recursos |

### 7.2 Beneficios Económicos

- **Reducción de pérdida de activos**: cada equipo está registrado, asignado y auditado
- **Menos mantenimientos correctivos**: la planificación preventiva reduce intervenciones de emergencia (típicamente **30-40% más costosas**)
- **Optimización de rutas**: el GPS permite asignar técnicos por proximidad
- **Reducción de horas administrativas**: reportes automáticos eliminan consolidación manual
- **Eliminación de costos de papel e impresión**: actas y formatos 100% digitales

### 7.3 Beneficios para la Toma de Decisiones

- **Visibilidad total** del estado de los activos: cuántos están activos, deteriorados, en mantenimiento
- **Métricas de rendimiento** por técnico: cumplimiento, velocidad, carga de trabajo
- **Tendencias**: evolución de mantenimientos mes a mes para anticipar necesidades
- **Inventario actualizado**: siempre disponible por sucursal, departamento o custodio
- **Dashboard ejecutivo** para gerencia con indicadores clave

### 7.4 Beneficios de Cumplimiento y Auditoría

- **Actas digitales firmadas** con validez documental
- **Historial inmutable** de asignaciones, traslados y bajas
- **Registro de consentimiento GPS** conforme a protección de datos
- **Trazabilidad de cada equipo** desde su alta hasta su baja
- **Evidencia fotográfica** de cada mantenimiento realizado
- **Validación de cédula ecuatoriana** para custodios

---

## 8. ESCALAS Y NÚMEROS DEL SISTEMA

| Métrica | Capacidad |
|---|---|
| Controladores REST (API) | 23 endpoints principales |
| Entidades de dominio | 30 entidades |
| Casos de uso | 55+ |
| Páginas web | 65 vistas Thymeleaf |
| Pantallas móvil | 16 screens |
| Migraciones BD | 20 versiones Flyway |
| Tipos de reporte exportable | 3 Excel + 4 PDF |
| Gráficos en dashboard | 8 gráficos interactivos |

---

## 9. REQUISITOS PARA PRODUCCIÓN

### Infraestructura Mínima

| Componente | Requerimiento |
|---|---|
| **Servidor API** | Java 17, 2GB RAM mínimo, Docker disponible |
| **Base de datos** | PostgreSQL 15+, 10GB disco inicial |
| **Servidor Web** | Java 17, 1GB RAM mínimo |
| **Red** | Puertos 8081 (web) y 8083 (API) accesibles |
| **Email** | Cuenta SMTP Gmail con contraseña de aplicación |
| **Firebase** | Proyecto FCM configurado para push notifications |
| **Dispositivos** | Android 6.0+ con GPS y cámara |

### Despliegue

- **Docker Compose** incluido para orquestación de contenedores
- **Flyway** gestiona migraciones automáticas de base de datos en producción
- **Perfiles de configuración**: `dev` y `prod` separados
- **Variables de entorno** para credenciales sensibles (DB, SMTP, Firebase)

---

## 10. ROADMAP POST-PRODUCCIÓN (Sugerencias)

| Fase | Mejora | Beneficio |
|---|---|---|
| Fase 2 | Dashboard con filtros por fecha y sucursal | Análisis más profundos |
| Fase 2 | Notificaciones por correo de mantenimientos vencidos | Proactividad |
| Fase 3 | Integración con sistema contable/ERP | Valorización de activos |
| Fase 3 | App iOS (Flutter ya es cross-platform) | Cobertura total de dispositivos |
| Fase 4 | Business Intelligence con Power BI/Tableau | Analítica avanzada |
| Fase 4 | Módulo de depreciación de activos | Control financiero |

---

## 11. CONCLUSIÓN

**CRESIO** es un sistema maduro y completo que cubre el **ciclo de vida integral de los activos tecnológicos**. Los tres componentes (API, Web, Móvil) trabajan de forma coordinada para ofrecer:

1. ✅ **Control total** sobre qué equipos tiene la empresa, dónde están y quién los tiene
2. ✅ **Mantenimiento planificado** que reduce costos y extiende la vida útil de los activos
3. ✅ **Documentación legal automatizada** (actas de custodia con firma digital)
4. ✅ **Trabajo de campo eficiente** con app móvil offline-first
5. ✅ **Supervisión y métricas** en tiempo real para la toma de decisiones
6. ✅ **Seguridad y auditoría** con roles, permisos y trazabilidad completa

El sistema está listo técnicamente para producción, con arquitectura robusta (Clean Architecture), base de datos con migraciones versionadas, contenedorización Docker y separación clara de perfiles dev/prod.

---

*Sistema desarrollado con Java 17, Spring Boot 4.0.2, PostgreSQL, Flutter y Firebase.*
