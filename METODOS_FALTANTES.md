# Métodos Faltantes en Interfaces RepositorioPuerto

Este documento lista exactamente qué métodos faltan en cada interfaz RepositorioPuerto que está siendo inyectada en casos de uso.

---

## 1. DepartamentoRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/DepartamentoRepositorioPuerto.java`  
**Usado por**: `DepartamentosUseCaseImpl`

### Métodos Faltantes (3):

```java
/**
 * Verifica si existe un departamento con el nombre especificado.
 * @param nombre el nombre del departamento
 * @return true si existe, false en caso contrario
 */
boolean existeNombre(String nombre);

/**
 * Verifica si existe otro departamento con el nombre especificado (excluyendo el actual).
 * @param nombre el nombre del departamento
 * @param idDepartamento el ID del departamento actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeNombreParaOtro(String nombre, int idDepartamento);

/**
 * Actualiza el estado de un departamento.
 * @param id el ID del departamento
 * @param departamento el departamento con el nuevo estado
 * @return el departamento con el estado actualizado
 */
Departamentos actualizarEstado(int id, Departamentos departamento);
```

### Métodos a Mejorar:

```java
// ACTUAL (firma incorrecta):
void actualizar(Departamentos Departamentos);

// DEBERÍA SER:
Departamentos actualizar(int id, Departamentos departamento);
```

---

## 2. CargosRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/CargosRepositorioPuerto.java`  
**Usado por**: `CargosUseCaseImpl`

**Estado**: PARCIALMENTE OK
- ✓ Tiene `buscarPorId()` como default method
- ✓ Tiene `listarTodos()` como default method
- ✓ Tiene `actualizar(int, Cargo)` con firma correcta
- ✓ Tiene `actualizarEstado(int, Cargo)` con firma correcta

### Métodos Faltantes (2):

```java
/**
 * Verifica si existe un cargo con el nombre especificado.
 * @param nombre el nombre del cargo
 * @return true si existe, false en caso contrario
 */
boolean existeNombre(String nombre);

/**
 * Verifica si existe otro cargo con el nombre especificado (excluyendo el actual).
 * @param nombre el nombre del cargo
 * @param idCargo el ID del cargo actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeNombreParaOtro(String nombre, int idCargo);
```

---

## 3. EquipoRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/EquipoRepositorioPuerto.java`  
**Usado por**: `EquiposUseCaseImpl`

**Estado**: MUY INCOMPLETA

### Métodos Faltantes (8):

```java
/**
 * Verifica si existe un equipo con el Código SAP (Activo Fijo) especificado.
 * @param codigo el código SAP
 * @return true si existe, false en caso contrario
 */
boolean existeCodigo(String codigo);

/**
 * Verifica si existe otro equipo con el Código SAP especificado (excluyendo el actual).
 * @param codigo el código SAP
 * @param idEquipo el ID del equipo actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeCodigoParaOtro(String codigo, int idEquipo);

/**
 * Verifica si existe un equipo con el serial especificado.
 * @param serial el serial del equipo
 * @return true si existe, false en caso contrario
 */
boolean existeSerial(String serial);

/**
 * Verifica si existe otro equipo con el serial especificado (excluyendo el actual).
 * @param serial el serial del equipo
 * @param idEquipo el ID del equipo actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeSerialParaOtro(String serial, int idEquipo);

/**
 * Verifica si existe un equipo con la dirección IP especificada.
 * @param ip la dirección IP
 * @return true si existe, false en caso contrario
 */
boolean existeIP(String ip);

/**
 * Verifica si existe otro equipo con la dirección IP especificada (excluyendo el actual).
 * @param ip la dirección IP
 * @param idEquipo el ID del equipo actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeIPParaOtro(String ip, int idEquipo);

/**
 * Verifica si existe un equipo con la dirección MAC especificada.
 * @param mac la dirección MAC
 * @return true si existe, false en caso contrario
 */
boolean existeMAC(String mac);

/**
 * Verifica si existe otro equipo con la dirección MAC especificada (excluyendo el actual).
 * @param mac la dirección MAC
 * @param idEquipo el ID del equipo actual a excluir
 * @return true si existe otro, false en caso contrario
 */
boolean existeMACParaOtro(String mac, int idEquipo);
```

### Métodos a Mejorar:

```java
// ACTUAL (para actualizar):
void actualizar(Equipos equipo);

// DEBERÍA HABER TAMBIÉN:
Equipos actualizar(int id, Equipos equipo);

// O CONSIDERAR CAMBIAR FIRMA PARA RETORNAR ENTIDAD:
Equipos actualizar(Equipos equipo);
```

---

## 4. UbicacionRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/UbicacionRepositorioPuerto.java`  
**Usado por**: `UbicacionesUseCaseImpl`

**Estado**: ✓ COMPLETA

- ✓ Tiene `buscarPorId()` como default method
- ✓ Tiene `listarTodos()` como default method
- ✓ Tiene `actualizar(int, Ubicacion)` con firma correcta
- ✓ Tiene `actualizarEstado(int, Ubicacion)` con firma correcta
- ✓ Tiene `existeNombre(String)` 
- ✓ Tiene `existeNombreParaOtro(String, int)`

**NO REQUIERE CAMBIOS** ✓

---

## 5. RolRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/RolRepositorioPuerto.java`  
**Usado por**: `RolesUseCaseImpl`

**Estado**: INCOMPLETA EN ALIAS

- ✓ Tiene `obtenerPorNombre(String)`
- ❌ NO TIENE un default method `buscarPorNombre()` (alias)

### Métodos a Agregar:

```java
/**
 * Busca un rol por su nombre.
 * Alias para obtenerPorNombre para compatibilidad.
 * @param nombre el nombre del rol
 * @return Optional con el rol si existe
 */
default Optional<Roles> buscarPorNombre(String nombre) {
    return obtenerPorNombre(nombre);
}

/**
 * Busca un rol por su ID.
 * Alias para obtenerPorId para compatibilidad.
 * @param id el ID del rol
 * @return Optional con el rol si existe
 */
default Optional<Roles> buscarPorId(int id) {
    return obtenerPorId(id);
}

/**
 * Obtiene todos los roles.
 * Alias para obtenerTodos para compatibilidad.
 * @return lista de todos los roles
 */
default List<Roles> listarTodos() {
    return obtenerTodos();
}
```

---

## 6. MarcaRepositorioPuerto

**Archivo**: `dominio/puertos/repositorios/MarcaRepositorioPuerto.java`  
**Usado por**: `MarcasUseCaseImpl`

**Estado**: REQUIERE VERIFICACIÓN

Necesita confirmar si tiene default methods para `buscarPorId()` y `listarTodos()`.

---

## Resumen: Métodos Faltantes por Interfaz

| Interfaz | Métodos Faltantes | Criticidad |
|---|---|---|
| DepartamentoRepositorioPuerto | 3 (existeNombre, existeNombreParaOtro, actualizarEstado) | 🔴 CRÍTICA |
| CargosRepositorioPuerto | 2 (existeNombre, existeNombreParaOtro) | 🔴 CRÍTICA |
| EquipoRepositorioPuerto | 8 (todos los existe*) | 🔴 CRÍTICA |
| UbicacionRepositorioPuerto | 0 | ✓ OK |
| RolRepositorioPuerto | 3 (default methods para alias) | 🟠 ALTA |
| MarcaRepositorioPuerto | TBD | ⚠️ VERIFICAR |

---

## Plan de Acción Recomendado

### Opción 1: Completar todas las interfaces RepositorioPuerto (Recomendado)
1. Agregar todos los métodos faltantes listados arriba
2. Agregar default methods para compatibilidad de nombres antiguos
3. Validar que todas las implementaciones tengan todos los métodos

### Opción 2: Revertir a interfaces antiguas
1. Cambiar inyecciones en casos de uso de RepositorioPuerto a interfaces antiguas
2. Mantener las antiguas interfaces como estaban
3. Eliminar gradualmente interfaces RepositorioPuerto

### Opción 3: Crear Adapters
1. Implementar `*RepositorioPuerto` como adapters
2. Que deleguen a las antiguas interfaces
3. Convertir el código gradualmente

