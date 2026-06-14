# CLAUDE.md — Reglas de trabajo para CRESIO

## Contexto del proyecto

CRESIO es un sistema de gestión de activos TIC.

Este módulo `consumogestionactivosapi` es un BFF MVC/Thymeleaf.

Arquitectura:

Browser → Thymeleaf/MVC puerto 8081 → RestClient → API Backend puerto 8084

No es el backend principal de negocio. La persistencia y reglas profundas viven en la API externa.

## Stack

* Java 17.
* Spring Boot.
* Thymeleaf.
* Bootstrap 5.
* PostgreSQL vía API externa.
* RestClient hacia API backend.
* Frontend Enterprise UI con clases `cui-*`.

## Reglas obligatorias

No modificar base de datos salvo que se solicite.
No modificar entidades, DTOs o servicios sin aprobación.
No crear endpoints nuevos sin aprobación.
No cambiar mappings existentes sin aprobación.
No cambiar nombres de inputs de formularios.
No romper rutas antiguas.
No tocar Flutter.
No tocar API backend salvo que se indique explícitamente.

## Flujo de trabajo

Antes de modificar archivos:

1. Analizar el código relacionado.
2. Explicar el plan.
3. Listar archivos a tocar.
4. Esperar aprobación.

Después de modificar:

1. Mostrar archivos modificados.
2. Explicar cambios.
3. Ejecutar:

   * `.\mvnw.cmd test`
   * `.\mvnw.cmd compile`
   * `git diff --check`
4. Reportar riesgos.

## Prioridades actuales

1. Timeouts en RestClient.
2. ControllerAdvice global.
3. Proteger `/setup`.
4. Tests básicos.
5. Refactor gradual de CustodiasControlador.
6. Resiliencia con Circuit Breaker.
7. Caché de catálogos.
8. Seguridad de sesión.
