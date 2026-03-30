# Plan de Corrección Detallado

## Introducción
Este documento proporciona un plan paso a paso para corregir los problemas de desalineación entre las interfaces RepositorioPuerto y los casos de uso.

---

## Evaluación Rápida de la Solución Óptima

### Opción A: Completar las Interfaces RepositorioPuerto (RECOMENDADA)
**Ventajas**:
- ✓ Mantiene la arquitectura de puertos limpia
- ✓ Las interfaces quedan completas y documentadas
- ✓ Future-proof: nueva arquitectura está lista

**Desventajas**:
- Trabajo considerable (agregar ~13 métodos)
- Requiere actualizar todas las implementaciones

**Esfuerzo**: 🟠 Medio (2-4 horas de desarrollo + testing)

### Opción B: Revertir a Interfaces Antiguas
**Ventajas**:
- ✓ Rápido (cambiar 5-6 inyecciones)
- ✓ Todo compilará inmediatamente

**Desventajas**:
- ❌ Abandona la arquitectura de puertos
- ❌ Deja código muerto (interfaces RepositorioPuerto sin usar)
- ❌ Contradice la dirección arquitectónica

**Esfuerzo**: 🟢 Mínimo (~30 minutos)

### Opción C: Adapters (Compromiso)
**Ventajas**:
- ✓ Mantiene la arquitectura de puertos
- ✓ Rápido de implementar
- ✓ Gradualmente se puede migrar completamente

**Desventajas**:
- ⚠️ Capa adicional de código
- ⚠️ Moderadamente complejo

**Esfuerzo**: 🟠 Medio (1-2 horas)

---

## RECOMENDACIÓN: Completar las Interfaces (Opción A)

### Razón
- El código ya está inyectado con las nuevas interfaces
- Indica que fue la intención arquitectónica
- Las nuevas interfaces están mejor diseñadas (ej: UbicacionRepositorioPuerto funciona bien)
- Completarlas es el camino a largo plazo correcto

---

## Plan de Implementación: Opción A

### Paso 1: Actualizar DepartamentoRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/DepartamentoRepositorioPuerto.java`

**Agregar al final de la interfaz**:

```java
    /**
     * Verifica si existe un departamento con el nombre especificado.
     * 
     * @param nombre el nombre del departamento
     * @return true si existe, false en caso contrario
     */
    boolean existeNombre(String nombre);
    
    /**
     * Verifica si existe otro departamento con el nombre especificado (excluyendo el actual).
     * 
     * @param nombre el nombre del departamento
     * @param idDepartamento el ID del departamento actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeNombreParaOtro(String nombre, int idDepartamento);
    
    /**
     * Actualiza el estado de un departamento.
     * 
     * @param id el ID del departamento
     * @param departamento el departamento con el nuevo estado
     * @return el departamento con el estado actualizado
     */
    Departamentos actualizarEstado(int id, Departamentos departamento);
```

**También cambiar la firma de actualizar**:
```java
// DE ESTO:
void actualizar(Departamentos Departamentos);

// A ESTO:
Departamentos actualizar(int id, Departamentos departamento);
```

---

### Paso 2: Actualizar CargosRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/CargosRepositorioPuerto.java`

**Agregar al final de la interfaz** (después del último método):

```java
    /**
     * Verifica si existe un cargo con el nombre especificado.
     * 
     * @param nombre el nombre del cargo
     * @return true si existe, false en caso contrario
     */
    boolean existeNombre(String nombre);
    
    /**
     * Verifica si existe otro cargo con el nombre especificado (excluyendo el actual).
     * 
     * @param nombre el nombre del cargo
     * @param idCargo el ID del cargo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeNombreParaOtro(String nombre, int idCargo);
```

---

### Paso 3: Actualizar EquipoRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/EquipoRepositorioPuerto.java`

**Agregar al final de la interfaz**:

```java
    /**
     * Verifica si existe un equipo con el Código SAP (Activo Fijo) especificado.
     * 
     * @param codigo el código SAP
     * @return true si existe, false en caso contrario
     */
    boolean existeCodigo(String codigo);
    
    /**
     * Verifica si existe otro equipo con el Código SAP especificado (excluyendo el actual).
     * 
     * @param codigo el código SAP
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeCodigoParaOtro(String codigo, int idEquipo);
    
    /**
     * Verifica si existe un equipo con el serial especificado.
     * 
     * @param serial el serial del equipo
     * @return true si existe, false en caso contrario
     */
    boolean existeSerial(String serial);
    
    /**
     * Verifica si existe otro equipo con el serial especificado (excluyendo el actual).
     * 
     * @param serial el serial del equipo
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeSerialParaOtro(String serial, int idEquipo);
    
    /**
     * Verifica si existe un equipo con la dirección IP especificada.
     * 
     * @param ip la dirección IP
     * @return true si existe, false en caso contrario
     */
    boolean existeIP(String ip);
    
    /**
     * Verifica si existe otro equipo con la dirección IP especificada (excluyendo el actual).
     * 
     * @param ip la dirección IP
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeIPParaOtro(String ip, int idEquipo);
    
    /**
     * Verifica si existe un equipo con la dirección MAC especificada.
     * 
     * @param mac la dirección MAC
     * @return true si existe, false en caso contrario
     */
    boolean existeMAC(String mac);
    
    /**
     * Verifica si existe otro equipo con la dirección MAC especificada (excluyendo el actual).
     * 
     * @param mac la dirección MAC
     * @param idEquipo el ID del equipo actual a excluir
     * @return true si existe otro, false en caso contrario
     */
    boolean existeMACParaOtro(String mac, int idEquipo);
```

---

### Paso 4: Actualizar RolRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/RolRepositorioPuerto.java`

**Agregar al final de la interfaz**:

```java
    /**
     * Busca un rol por su nombre.
     * Alias para obtenerPorNombre para compatibilidad.
     * 
     * @param nombre el nombre del rol
     * @return Optional con el rol si existe
     */
    default Optional<Roles> buscarPorNombre(String nombre) {
        return obtenerPorNombre(nombre);
    }
    
    /**
     * Busca un rol por su ID.
     * Alias para obtenerPorId para compatibilidad.
     * 
     * @param id el ID del rol
     * @return Optional con el rol si existe
     */
    default Optional<Roles> buscarPorId(int id) {
        return obtenerPorId(id);
    }
    
    /**
     * Obtiene todos los roles.
     * Alias para obtenerTodos para compatibilidad.
     * 
     * @return lista de todos los roles
     */
    default List<Roles> listarTodos() {
        return obtenerTodos();
    }
```

---

### Paso 5: Verificar y Actualizar las Implementaciones

Para cada interfaz actualizada, necesitas ir a la implementación (generalmente en `infraestructura/adaptadores/`) y:

1. Implementar los métodos nuevos
2. Verificar que haya una correspondencia con los métodos en `IXxxRepositorio` antigua

**Ubicaciones típicas de implementaciones**:
```
infraestructura/adaptadores/repositorios/
├── DepartamentoRepositorioAdaptador.java
├── CargosRepositorioAdaptador.java
├── EquipoRepositorioAdaptador.java
├── RolRepositorioAdaptador.java
└── ...
```

**Ejemplo de cómo implementar** (para `DepartamentoRepositorioAdaptador`):

```java
@Override
public boolean existeNombre(String nombre) {
    // Buscar en la base de datos si existe un departamento con este nombre
    // Puede reutilizar lógica de la antigua interfaz
}

@Override
public boolean existeNombreParaOtro(String nombre, int idDepartamento) {
    // Buscar en la base de datos si existe otro departamento (diferente al ID) con este nombre
}

@Override
public Departamentos actualizarEstado(int id, Departamentos departamento) {
    // Actualizar el estado del departamento
    // Puede delegar a métodos internos
}
```

---

### Paso 6: Compilar y Verificar

```bash
cd gestionactivosapi
./mvnw clean compile
```

Debería compilar sin errores después de estos cambios.

---

## Secuencia de Ejecución Recomendada

1. **Primero**: MarcaRepositorioPuerto (mínimo cambio)
2. **Segundo**: RolRepositorioPuerto (solo métodos default)
3. **Tercero**: CargosRepositorioPuerto (2 métodos nuevos)
4. **Cuarto**: DepartamentoRepositorioPuerto (3 métodos nuevos + cambio firma)
5. **Quinta**: EquipoRepositorioPuerto (8 métodos nuevos - la más grande)

---

## Alternativa Rápida: Opción B (Si hay prisa)

Si no hay tiempo para completar las interfaces, cambiar las inyecciones:

### Para DepartamentosUseCaseImpl:
```java
// DE ESTO:
private final DepartamentoRepositorioPuerto departamentoRepositorio;

// A ESTO:
private final IDepartamentosRepositorio departamentoRepositorio;
```

Y cambiar calls de método que no coincidan (por ejemplo: `obtenerPorId()` → `buscarPorId()`).

Sería necesario hacer esto para:
- DepartamentosUseCaseImpl
- CargosUseCaseImpl
- EquiposUseCaseImpl
- RolesUseCaseImpl
- MarcasUseCaseImpl (si es necesario)

---

## Conclusión

**Recomendación Final**: Completar las interfaces RepositorioPuerto (Opción A).
- Mantiene la arquitectura limpia
- Es el camino a largo plazo correcto
- El esfuerzo es justificable

