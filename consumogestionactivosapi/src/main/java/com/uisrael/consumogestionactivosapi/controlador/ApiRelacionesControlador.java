package com.uisrael.consumogestionactivosapi.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MarcasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICargosServicio;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;
import com.uisrael.consumogestionactivosapi.service.IRolesServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/relaciones")
public class ApiRelacionesControlador {

    private final IDepartamentosServicio servicioDepartamento;
    private final ICargosServicio servicioCargo;
    private final IUbicacionesServicio servicioUbicacion;
    private final IMarcasServicio servicioMarca;
    private final ICategoriaEquiposServicio servicioCategoria;
    private final IRolesServicio servicioRol;

    @GetMapping("/departamentos")
    public List<DepartamentosResponseDTO> listarDepartamentos() {
        return servicioDepartamento.listarDepartamentos().stream()
                .filter(DepartamentosResponseDTO::isEstado)
                .toList();
    }

    @PostMapping("/departamentos")
    public ResponseEntity<?> crearDepartamento(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        if (servicioDepartamento.nombreExiste(nombre.trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un departamento con ese nombre"));
        }
        DepartamentosRequestDTO dto = new DepartamentosRequestDTO();
        dto.setNombre(nombre.trim());
        dto.setEstado(true);
        // Ubicacion por defecto (sin asignar)
        UbicacionesRequestDTO ub = new UbicacionesRequestDTO();
        ub.setIdUbicacion(0);
        dto.setFkUbicacion(ub);
        servicioDepartamento.crearDepartamento(dto);
        // Retornar la lista actualizada para obtener el ID generado
        List<DepartamentosResponseDTO> lista = servicioDepartamento.listarDepartamentos().stream()
                .filter(d -> d.isEstado() && d.getNombre().equalsIgnoreCase(nombre.trim()))
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Departamento creado"));
    }

    @GetMapping("/cargos")
    public List<CargosResponseDTO> listarCargos() {
        return servicioCargo.listarCargos().stream()
                .filter(CargosResponseDTO::isEstado)
                .toList();
    }

    @PostMapping("/cargos")
    public ResponseEntity<?> crearCargo(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        String idDepartamentoRaw = body.get("idDepartamento");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        if (idDepartamentoRaw == null || idDepartamentoRaw.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe seleccionar un departamento"));
        }
        int idDepartamento;
        try {
            idDepartamento = Integer.parseInt(idDepartamentoRaw.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Departamento invalido"));
        }
        if (idDepartamento <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe seleccionar un departamento"));
        }
        if (servicioCargo.nombreExiste(nombre.trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un cargo con ese nombre"));
        }
        CargosRequestDTO dto = new CargosRequestDTO();
        dto.setNombre(nombre.trim());
        dto.setEstado(true);
        DepartamentosRequestDTO departamento = new DepartamentosRequestDTO();
        departamento.setIdDepartamento(idDepartamento);
        dto.setFkDepartamento(departamento);
        servicioCargo.crearCargo(dto);
        List<CargosResponseDTO> lista = servicioCargo.listarCargos().stream()
                .filter(c -> c.isEstado()
                        && c.getNombre().equalsIgnoreCase(nombre.trim())
                        && c.getFkDepartamento() != null
                        && c.getFkDepartamento().getIdDepartamento() == idDepartamento)
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Cargo creado"));
    }

    @GetMapping("/ubicaciones")
    public List<UbicacionesResponseDTO> listarUbicaciones() {
        return servicioUbicacion.listarUbicaciones().stream()
                .filter(UbicacionesResponseDTO::isEstado)
                .toList();
    }

    @PostMapping("/ubicaciones")
    public ResponseEntity<?> crearUbicacion(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        String agencia = body.get("agencia");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        if (agencia == null || agencia.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La agencia es obligatoria"));
        }
        if (servicioUbicacion.nombreExiste(nombre.trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe una ubicacion con ese nombre"));
        }
        UbicacionesRequestDTO dto = new UbicacionesRequestDTO();
        dto.setNombre(nombre.trim());
        dto.setAgencia(agencia.trim());
        dto.setEstado(true);
        servicioUbicacion.crearUbicacion(dto);
        List<UbicacionesResponseDTO> lista = servicioUbicacion.listarUbicaciones().stream()
                .filter(u -> u.isEstado() && u.getNombre().equalsIgnoreCase(nombre.trim()))
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Ubicacion creada"));
    }

    // ── MARCAS ──────────────────────────────────────────────

    @GetMapping("/marcas")
    public List<MarcasResponseDTO> listarMarcas() {
        return servicioMarca.listarMarca();
    }

    @PostMapping("/marcas")
    public ResponseEntity<?> crearMarca(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        try {
            MarcasRequestDTO dto = new MarcasRequestDTO();
            dto.setNombre(nombre.trim());
            dto.setEstado(true);
            servicioMarca.nuevaMarca(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ex.getMessage()));
        }
        List<MarcasResponseDTO> lista = servicioMarca.listarMarca().stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre.trim()))
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Marca creada"));
    }

    // ── CATEGORIAS EQUIPO ───────────────────────────────────

    @GetMapping("/categorias")
    public List<CategoriaEquiposResponseDTO> listarCategorias() {
        return servicioCategoria.listarCategoriaEquipo();
    }

    @PostMapping("/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        boolean existe = servicioCategoria.listarCategoriaEquipo().stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
        if (existe) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe una categoria con ese nombre"));
        }
        try {
            CategoriaEquiposRequestDTO dto = new CategoriaEquiposRequestDTO();
            dto.setNombre(nombre.trim());
            dto.setEstado(true);
            servicioCategoria.nuevoCategoriaEquipo(dto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Error al crear la categoria"));
        }
        List<CategoriaEquiposResponseDTO> lista = servicioCategoria.listarCategoriaEquipo().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre.trim()))
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Categoria creada"));
    }

    // ── ROLES ───────────────────────────────────────────────

    @GetMapping("/roles")
    public List<RolesResponseDTO> listarRoles() {
        return servicioRol.listarRol().stream()
                .filter(RolesResponseDTO::isEstado)
                .toList();
    }

    @PostMapping("/roles")
    public ResponseEntity<?> crearRol(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        boolean existe = servicioRol.listarRol().stream()
                .filter(RolesResponseDTO::isEstado)
                .anyMatch(r -> r.getNombre().equalsIgnoreCase(nombre.trim()));
        if (existe) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un rol con ese nombre"));
        }
        try {
            RolesRequestDTO dto = new RolesRequestDTO();
            dto.setNombre(nombre.trim());
            dto.setEstado(true);
            servicioRol.nuevoRol(dto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Error al crear el rol"));
        }
        List<RolesResponseDTO> lista = servicioRol.listarRol().stream()
                .filter(r -> r.isEstado() && r.getNombre().equalsIgnoreCase(nombre.trim()))
                .toList();
        if (!lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista.get(lista.size() - 1));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Rol creado"));
    }
}
