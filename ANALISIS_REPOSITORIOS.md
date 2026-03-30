# Análisis Comparativo: Interfaces Antiguas vs Nuevas (RepositorioPuerto)

## ⚠️ RESUMEN EJECUTIVO - PROBLEMA CRÍTICO

### El Problema
**Existe una desalineación arquitectónica severa**: Los casos de uso están siendo inyectados con las nuevas interfaces `RepositorioPuerto`, pero estas interfaces **no tienen todos los métodos necesarios** que el código necesita para funcionar.

### Resultado
- ❌ **DepartamentosUseCaseImpl** no compilará (faltan 3 métodos)
- ❌ **CargosUseCaseImpl** no compilará (faltan 2 métodos)
- ❌ **EquiposUseCaseImpl** no compilará (faltan 6+ métodos)
- ❌ **RolesUseCaseImpl** no compilará (método con nombre incorrecto)
- ✓ **UbicacionesUseCaseImpl** SÍ compilará (solo esta está completa)
- ✓ **SetupControlador** SÍ compilará (usa interfaces antiguas)

### Causa
La migración a arquitectura de puertos fue **incompleta**:
- Se crearon las nuevas interfaces (`*RepositorioPuerto`)
- Se inyectaron en los casos de uso
- **PERO** No se implementaron todos los métodos antiguos en las nuevas interfaces

### Soluciones Posibles
1. Completar todas las interfaces `RepositorioPuerto` con los métodos faltantes
2. Usar interfaces antiguas en lugar de las nuevas (revertir migración)
3. Crear adapters que implementen `RepositorioPuerto` delegando a interfaces antiguas

---

## 1. Comparación de Interfaces por Entidad

### ⚠️ DESCUBRIMIENTO CRÍTICO: Métodos Default
Algunas interfaces RepositorioPuerto tienen **métodos default que son aliases** para compatibilidad:

**CargosRepositorioPuerto y EquipoRepositorioPuerto incluyen**:
```java
default Optional<T> buscarPorId(int id) {
    return obtenerPorId(id);
}

default List<T> listarTodos() {
    return obtenerTodos();
}
```

Esto significa que aunque el código usa `buscarPorId()` y `listarTodos()`, estas llamadas SÍ funcionarán mediante los métodos default.  
**PERO**: Los métodos de validación (existeNombre, existeCodigo, etc.) NO tienen aliases.

---

### 1.1 DEPARTAMENTOS
**Ubicación antigua**: `dominio/repositorios/IDepartamentosRepositorio.java`  
**Ubicación nueva**: `dominio/puertos/repositorios/DepartamentoRepositorioPuerto.java`

#### Métodos en IDepartamentosRepositorio (ANTIGUA):
```java
- guardar(Departamentos)                          ✓
- buscarPorId(int)                               
- listarTodos()                                  
- actualizar(int id, Departamentos)              
- actualizarEstado(int id, Departamentos)        ❌ NO existe en la nueva
- existeNombre(String)                           ❌ NO existe en la nueva
- existeNombreParaOtro(String, int)             ❌ NO existe en la nueva
```

#### Métodos en DepartamentoRepositorioPuerto (NUEVA):
```java
- guardar(Departamentos)                          ✓
- obtenerPorId(Integer)                          ⚠️ (cambio: nombre + tipo parámetro)
- obtenerTodos()                                 ⚠️ (cambio: nombre)
- actualizar(Departamentos)                      ⚠️ (cambio: firma sin id)
- eliminar(Integer)                              ✓ (nuevo)
- obtenerPorNombre(String)                       ✓ (nuevo)
- obtenerActivos()                               ✓ (nuevo)
```

**FALTA EN LA NUEVA**:
- `actualizarEstado()` - Necesario para actualizar solo el estado
- `existeNombre()` - Necesario para validar nombres únicos
- `existeNombreParaOtro()` - Necesario para validar nombres únicos excluyendo un ID

---

### 1.2 USUARIOS
**Ubicación antigua**: `dominio/repositorios/IUsuariosRepositorio.java`  
**Ubicación nueva**: `dominio/puertos/repositorios/UsuarioRepositorioPuerto.java`

#### Métodos en IUsuariosRepositorio (ANTIGUA):
```java
- guardar(Usuarios)                              ✓
- buscarPorId(int)                               
- listarTodos()                                  
- eliminar(int)                                  ✓
- buscarPorCorreo(String)                        
```

#### Métodos en UsuarioRepositorioPuerto (NUEVA):
```java
- guardar(Usuarios)                              ✓
- obtenerPorId(Integer)                          ⚠️ (cambio: nombre + tipo)
- obtenerTodos()                                 ⚠️ (cambio: nombre)
- actualizar(Usuarios)                           ✓ (nuevo)
- eliminar(Integer)                              ✓
- obtenerPorNombreUsuario(String)               ✓ (nuevo)
- obtenerPorCorreo(String)                       ⚠️ (cambio: nombre)
- obtenerActivos()                               ✓ (nuevo)
- obtenerPorRol(Integer)                         ✓ (nuevo)
- existePorCorreo(String)                        ✓ (nuevo)
```

**ESTADO**: Más completa que la antigua, pero con cambios de nombres

---

### 1.3 MARCAS
**Ubicación antigua**: `dominio/repositorios/IMarcasRepositorio.java`  
**Ubicación nueva**: `dominio/puertos/repositorios/MarcaRepositorioPuerto.java`

#### Métodos en IMarcasRepositorio (ANTIGUA):
```java
- guardar(Marcas)                                ✓
- buscarPorId(int)                               
- listarTodos()                                  
- actualizar(int id, Marcas)                     
- eliminar(int)                                  ✓
```

#### Métodos en MarcaRepositorioPuerto (NUEVA):
```java
- guardar(Marcas)                                ✓
- obtenerPorId(Integer)                          ⚠️ (cambio: nombre + tipo)
- obtenerTodas()                                 ⚠️ (cambio: nombre)
- actualizar(Marcas)                             ⚠️ (cambio: firma sin id)
- eliminar(Integer)                              ✓
- obtenerPorNombre(String)                       ✓ (nuevo)
- obtenerActivas()                               ✓ (nuevo)
```

**ESTADO**: Base similar pero con cambios de nombres en métodos clave

---

### 1.4 CARGOS
**Ubicación antigua**: `dominio/repositorios/ICargosRepositorio.java`  
**Ubicación nueva**: `dominio/puertos/repositorios/CargosRepositorioPuerto.java`

#### Métodos en ICargosRepositorio (ANTIGUA):
```java
- guardar(Cargos)                                ✓
- buscarPorId(int)                               
- listarTodos()                                  
- actualizar(int id, Cargos)                     
- actualizarEstado(int id, Cargos)              ❌ NO existe en la nueva
- existeNombre(String)                           ❌ NO existe en la nueva
- existeNombreParaOtro(String, int)             ❌ NO existe en la nueva
```

#### Por revisar: CargosRepositorioPuerto

---

## 2. Problemas Identificados en Uso Actual

## 2. Problemas Identificados en Uso Actual

### 2.1 DepartamentosUseCaseImpl
**Archivo**: `aplicacion/casosuso/impl/DepartamentosUseCaseImpl.java`  
**Inyecta**: `DepartamentoRepositorioPuerto` (NUEVA)  
**Intenta usar**:

```java
❌ departamentoRepositorio.existeNombre()           // método NO existe
⚠️ departamentoRepositorio.buscarPorId()            // default method ✓ (existe)
⚠️ departamentoRepositorio.listarTodos()            // default method ✓ (existe)
❌ departamentoRepositorio.existeNombreParaOtro()   // método NO existe
❌ departamentoRepositorio.actualizar(id, depto)    // firma incorrecta
❌ departamentoRepositorio.actualizarEstado()       // método NO existe
```

**Resultado**: El código NO COMPILARÁ ❌ (faltan 3 métodos)


---

### 2.2 CargosUseCaseImpl
**Archivo**: `aplicacion/casosuso/impl/CargosUseCaseImpl.java`  
**Inyecta**: `CargosRepositorioPuerto` (NUEVA)  
**Intenta usar**:

```java
❌ cargosRepositorio.existeNombre()                // método NO existe
⚠️ cargosRepositorio.buscarPorId()                // default method ✓ (existe)
⚠️ cargosRepositorio.listarTodos()                // default method ✓ (existe)
❌ cargosRepositorio.existeNombreParaOtro()       // método NO existe
⚠️ cargosRepositorio.actualizar(id, cargo)        // SÍ existe con esta firma ✓
⚠️ cargosRepositorio.actualizarEstado()           // SÍ existe ✓
```

**Resultado**: El código NO COMPILARÁ ❌ (faltan 2 métodos de validación)

**Nota**: `CargosRepositorioPuerto` es más completa que otras, ya que SÍ tiene `actualizarEstado()` y tiene default methods para compatibilidad.


---

### 2.3 MarcasUseCaseImpl
**Archivo**: `aplicacion/casosuso/impl/MarcasUseCaseImpl.java`  
**Inyecta**: `MarcaRepositorioPuerto` (NUEVA)  
**Intenta usar**:

```java
⚠️ marcaRepositorio.buscarPorId()                 // PROBABLEMENTE default method en MarcaRepositorioPuerto
⚠️ marcaRepositorio.listarTodos()                 // PROBABLEMENTE default method en MarcaRepositorioPuerto
⚠️ marcaRepositorio.actualizar(id, marcas)        // firma puede variar
```

**Resultado**: PROBABLEMENTE COMPILARÁ (requiere verificar si MarcaRepositorioPuerto tiene métodos default)


---

### 2.4 EquiposUseCaseImpl
**Archivo**: `aplicacion/casosuso/impl/EquiposUseCaseImpl.java`  
**Inyecta**: `EquipoRepositorioPuerto` (NUEVA)  
**Intenta usar**:

```java
❌ equipoRepositorio.existeCodigo()               // método NO existe en RepositorioPuerto
❌ equipoRepositorio.existeSerial()               // método NO existe en RepositorioPuerto
❌ equipoRepositorio.existeIP()                   // método NO existe en RepositorioPuerto
❌ equipoRepositorio.existeMAC()                  // método NO existe en RepositorioPuerto
❌ equipoRepositorio.existeCodigoParaOtro()       // método NO existe en RepositorioPuerto
❌ equipoRepositorio.existeSerialParaOtro()       // método NO existe en RepositorioPuerto
⚠️ equipoRepositorio.buscarPorId()                // default method ✓ (existe)
⚠️ equipoRepositorio.listarTodos()                // default method ✓ (existe)
```

**Resultado**: El código NO COMPILARÁ ❌ (faltan 6 métodos de validación)

**NOTA CRÍTICA**: `EquipoRepositorioPuerto` es muy incompleta. Falta casi toda la lógica de validación de duplicados que existe en `IEquiposRepositorio`.


---

### 2.5 SetupControlador
**Archivo**: `presentacion/controladores/SetupControlador.java`  
**Inyecta**: `IDepartamentosRepositorio` (ANTIGUA) ✓
**Métodos usados**:
```java
✓ usuariosRepositorio.listarTodos()               // existe en la antigua
✓ departamentosRepositorio.existeNombre()         // existe en la antigua
✓ departamentosRepositorio.listarTodos()          // existe en la antigua
```

**Resultado**: El código SÍ COMPILARÁ ✓

---

## 3. Tabla Resumen: Estado de Compilación por Componente

### UseCases que inyectan RepositorioPuerto
| Componente | Repositorio Inyectado | Métodos Faltantes | Estado | Notas |
|---|---|---|---|---|
| DepartamentosUseCaseImpl | DepartamentoRepositorioPuerto | `existeNombre`, `existeNombreParaOtro`, `actualizarEstado` | ❌ NO | Faltan métodos de validación |
| CargosUseCaseImpl | CargosRepositorioPuerto | `existeNombre`, `existeNombreParaOtro` | ❌ NO | Tiene métodos default + actualizar correcto |
| MarcasUseCaseImpl | MarcaRepositorioPuerto | Verificar | ⚠️ ? | Necesita confirmación |
| EquiposUseCaseImpl | EquipoRepositorioPuerto | `existeCodigo`, `existeSerial`, `existeIP`, `existeMAC`, más variantes ParaOtro (6+ métodos) | ❌ NO | MUY INCOMPLETA |
| **UbicacionesUseCaseImpl** | **UbicacionRepositorioPuerto** | **NINGUNO** | **✓ SÍ** | **TIENE metodosdefault + validaciones + estado** |
| RolesUseCaseImpl | RolRepositorioPuerto | `buscarPorNombre` (debería existir como alias) | ❌ NO | Usa `obtenerPorNombre` pero no tiene alias `buscarPorNombre` |

### Controladores que inyectan interfaces antiguas
| Componente | Repositorio Inyectado | Estado | Notas |
|---|---|---|---|
| SetupControlador | IDepartamentosRepositorio, IUsuariosRepositorio, IRolesRepositorio | ✓ SÍ | Compilará, interfaces antiguas tienen todos los métodos |

---

### Cambios de Nombres
| Método Antiguo | Método Nuevo | Impacto |
|---|---|---|
| `buscarPorId()` | `obtenerPorId()` | ⚠️ Todos los casos de uso actualizar |
| `listarTodos()` | `obtenerTodos()` | ⚠️ Todos los casos de uso actualizar |
| `actualizar(id, entity)` | `actualizar(entity)` | ⚠️ Cambio de firma |
| `buscarPorCorreo()` | `obtenerPorCorreo()` | ⚠️ Cambio de nombre |

### Métodos Eliminados (PROBLEMA CRÍTICO)
| Método | Entidades Afectadas | Necesidad |
|---|---|---|
| `actualizarEstado()` | Departamentos, Equipos, Ubicaciones, Cargos, Custodios, Custodias | Crítica - actualizar solo estado |
| `existeNombre()` | Departamentos, Cargos, Ubicaciones | Crítica - validación de duplicados |
| `existeNombreParaOtro()` | Departamentos, Cargos, Ubicaciones | Crítica - validación excluyendo ID actual |

---

## 4. Recomendaciones

### OPCIÓN A: Agregar métodos que faltan a RepositorioPuerto
**Ventaja**: Mantener la arquitectura nueva de puertos  
**Desventaja**: Aumentar la interfaz nuevamente

**Métodos a agregar a cada RepositorioPuerto**:
```java
// Validación de nombres duplicados
boolean existeNombre(String nombre);
boolean existeNombreParaOtro(String nombre, Integer id);

// Actualizar estado separadamente
void actualizarEstado(Integer id, boolean estado);
// O pasar la entidad:
void actualizarEstado(Integer id, Entidad entidad);
```

### OPCIÓN B: Actualizar todos los casos de uso y controladores
**Ventaja**: Usar la nouvelle interfaz consistentemente  
**Desventaja**: Más cambios, necesidad de refactoring lógica

**Requiere**:
1. Actualizar nombres de métodos (`obtenerPorId` en lugar de `buscarPorId`, etc.)
2. Refactorizar lógica de validación (crear métodos nuevos para validar nombres)
3. Actualizar firmas de `actualizar()` para recibir la entidad completa

### OPCIÓN C: Híbrida (RECOMENDADA)
1. Mantener ambas interfaces por ahora
2. Create adapter class que implemente RepositorioPuerto usando la antigua interfaz
3. Migrar gradualmente caso de uso por caso de uso

---

## 5. Archivos Involucrados

### Interfaces Antiguas (Todavía en uso)
```
dominio/repositorios/
├── IDepartamentosRepositorio.java      ⚠️ usado en SetupControlador
├── ICargosRepositorio.java
├── IUsuariosRepositorio.java           ⚠️ usado en SetupControlador
├── IMarcasRepositorio.java
├── IEquiposRepositorio.java
├── IRolesRepositorio.java              ⚠️ usado en SetupControlador
├── IUbicacionesRepositorio.java
├── ICustodiosRepositorio.java
├── ICustodiasRepositorio.java
├── ICategoriaEquiposRepositorio.java
├── IMantenimientosRepositorio.java
├── ITicketsRepositorio.java
└── ... (más)
```

### Interfaces Nuevas (Sin todos los métodos)
```
dominio/puertos/repositorios/
├── DepartamentoRepositorioPuerto.java  ❌ faltan métodos
├── CargosRepositorioPuerto.java        ❌ probablemente falten métodos
├── UsuarioRepositorioPuerto.java       ⚠️ renombramientos
├── MarcaRepositorioPuerto.java         ⚠️ renombramientos
├── EquipoRepositorioPuerto.java
├── RolRepositorioPuerto.java
├── UbicacionRepositorioPuerto.java
├── CustodioRepositorioPuerto.java
├── CustodiasRepositorioPuerto.java
├── CategoriaRepositorioPuerto.java
├── MantenimientoRepositorioPuerto.java
├── TicketRepositorioPuerto.java
└── ... (más)
```

### Casos de Uso Afectados
```
aplicacion/casosuso/impl/
├── DepartamentosUseCaseImpl.java        ❌ COMPILARÁ CON ERROR
├── CargosUseCaseImpl.java               ❌ PROBABLEMENTE ERRORES
├── EquiposUseCaseImpl.java              
├── UbicacionesUseCaseImpl.java          
├── CustodiosUseCaseImpl.java            
├── CustodiasUseCaseImpl.java            
└── ... (más)
```

### Controladores Usando Interfaces Antiguas
```
presentacion/controladores/
├── SetupControlador.java               ✓ COMPILARÁ
├── y posiblemente otros...
```

---

## 6. Conclusión

**El problema principal es**:
> Las interfaces nuevas (RepositorioPuerto) **no están completas**. Aunque algunas tienen métodos default para compatibilidad con nombres antiguos, **faltan métodos críticos de validación** que el código actual necesita:

**Métodos FALTANTES críticos**:
1. **Validación de nombres únicos**: `existeNombre()`, `existeNombreParaOtro()`
2. **Validación de duplicados específicos**: `existeCodigo()`, `existeSerial()`, `existeIP()`, `existeMAC()` y sus variantes ParaOtro
3. **Actualización de estado**: `actualizarEstado()` en algunas interfaces

**Impacto en Compilación**:
| Componente | Inyecta | Estado | Razón |
|---|---|---|---|
| DepartamentosUseCaseImpl | DepartamentoRepositorioPuerto | ❌ NO COMPILARÁ | Faltan: existeNombre, existeNombreParaOtro, actualizarEstado |
| CargosUseCaseImpl | CargosRepositorioPuerto | ❌ NO COMPILARÁ | Faltan: existeNombre, existeNombreParaOtro |
| MarcasUseCaseImpl | MarcaRepositorioPuerto | ⚠️ verificar | Depende de si tiene métodos default |
| EquiposUseCaseImpl | EquipoRepositorioPuerto | ❌ NO COMPILARÁ | Faltan: 6 métodos de validación (existe*) |
| UbicacionesUseCaseImpl | UbicacionRepositorioPuerto | ⚠️ verificar | Probablemente falten validaciones |
| SetupControlador | IDepartamentosRepositorio (ANTIGUA) | ✓ COMPILARÁ | Usa interfaz antigua que tiene todos los métodos |

**Conclusión**:
- Los controladores y casos de uso que usan **interfaces antiguas (IDepartamentosRepositorio, etc.)** SÍ compilarán ✓
- Los casos de uso que usan **interfaces nuevas (RepositorioPuerto)** NO compilarán ❌
- Esto es un **problema de migración incompleta** de la arquitectura de puertos

