package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.SyncEmpleadosCambioJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.SyncEmpleadosEjecucionJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ISyncEmpleadosCambioJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ISyncEmpleadosEjecucionJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.sync.EmpleadoSyncDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.AlertaCustodioInactivoDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.CambioSyncDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.EstadoSincronizacionDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.SincronizacionResultadoDTO;

/**
 * Sincroniza empleados desde una fuente externa (JSON) hacia la tabla local
 * de custodios (upsert por cedula). No consulta la fuente en vivo durante las
 * asignaciones: CRESIO valida contra los datos locales.
 *
 * Reglas:
 * - Empleado nuevo y activo: crea el custodio (origen_sync = true).
 * - Empleado existente: actualiza nombre/correo/telefono/cargo si cambiaron.
 * - Empleado inactivo en la fuente: inactiva el custodio y, si mantiene
 *   custodias activas, registra una ALERTA_ACTIVOS.
 * - Cargos se resuelven por nombre ignorando mayusculas y tildes; si no
 *   existen se registra una ADVERTENCIA (no se crean catalogos automaticamente).
 */
@Service
public class SincronizacionEmpleadosService {

    private static final Logger log = LoggerFactory.getLogger(SincronizacionEmpleadosService.class);

    public static final String TIPO_CREADO = "CREADO";
    public static final String TIPO_ACTUALIZADO = "ACTUALIZADO";
    public static final String TIPO_INACTIVADO = "INACTIVADO";
    public static final String TIPO_REACTIVADO = "REACTIVADO";
    public static final String TIPO_ADVERTENCIA = "ADVERTENCIA";
    public static final String TIPO_ALERTA_ACTIVOS = "ALERTA_ACTIVOS";

    private final ICustodiosJpaRepositorio custodiosRepo;
    private final ICustodiasJpaRepositorio custodiasRepo;
    private final ICargosJpaRepositorio cargosRepo;
    private final ISyncEmpleadosEjecucionJpaRepositorio ejecucionRepo;
    private final ISyncEmpleadosCambioJpaRepositorio cambioRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${empleados.sync.url:}")
    private String fuenteUrl;

    @Value("${empleados.sync.archivo:}")
    private String fuenteArchivo;

    public SincronizacionEmpleadosService(ICustodiosJpaRepositorio custodiosRepo,
            ICustodiasJpaRepositorio custodiasRepo,
            ICargosJpaRepositorio cargosRepo,
            ISyncEmpleadosEjecucionJpaRepositorio ejecucionRepo,
            ISyncEmpleadosCambioJpaRepositorio cambioRepo) {
        this.custodiosRepo = custodiosRepo;
        this.custodiasRepo = custodiasRepo;
        this.cargosRepo = cargosRepo;
        this.ejecucionRepo = ejecucionRepo;
        this.cambioRepo = cambioRepo;
    }

    public boolean fuenteConfigurada() {
        return !fuenteUrl.isBlank() || !fuenteArchivo.isBlank();
    }

    public String describirFuente() {
        if (!fuenteUrl.isBlank()) {
            return "URL: " + fuenteUrl;
        }
        if (!fuenteArchivo.isBlank()) {
            return "Archivo: " + fuenteArchivo;
        }
        return "Sin fuente configurada (EMPLEADOS_SYNC_URL o EMPLEADOS_SYNC_ARCHIVO)";
    }

    /** Lee la fuente configurada (URL o archivo) y sincroniza. */
    public SincronizacionResultadoDTO sincronizarDesdeFuente(String ejecutadoPor) {
        String json;
        String origen;
        if (!fuenteUrl.isBlank()) {
            origen = "URL";
            json = RestClient.create().get().uri(fuenteUrl).retrieve().body(String.class);
        } else if (!fuenteArchivo.isBlank()) {
            origen = "ARCHIVO";
            try {
                json = Files.readString(Path.of(fuenteArchivo), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el archivo de empleados: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalStateException(
                    "No hay fuente de empleados configurada. Defina EMPLEADOS_SYNC_URL o EMPLEADOS_SYNC_ARCHIVO.");
        }
        return sincronizar(parsearEmpleados(json), origen, ejecutadoPor);
    }

    /** Acepta un array raiz o un objeto con la clave "empleados". */
    public List<EmpleadoSyncDTO> parsearEmpleadosDesdeJson(String json) {
        return parsearEmpleados(json);
    }

    private List<EmpleadoSyncDTO> parsearEmpleados(String json) {
        try {
            JsonNode raiz = objectMapper.readTree(json);
            JsonNode lista = raiz.isArray() ? raiz : raiz.get("empleados");
            if (lista == null || !lista.isArray()) {
                throw new IllegalArgumentException(
                        "El JSON debe ser un array de empleados o un objeto con la clave \"empleados\".");
            }
            List<EmpleadoSyncDTO> empleados = new ArrayList<>();
            for (JsonNode nodo : lista) {
                empleados.add(objectMapper.treeToValue(nodo, EmpleadoSyncDTO.class));
            }
            return empleados;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON de empleados invalido: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SincronizacionResultadoDTO sincronizar(List<EmpleadoSyncDTO> empleados, String origen, String ejecutadoPor) {
        SyncEmpleadosEjecucionJpa ejecucion = new SyncEmpleadosEjecucionJpa();
        ejecucion.setEjecutadoEn(LocalDateTime.now());
        ejecucion.setOrigen(origen);
        ejecucion.setEjecutadoPor(ejecutadoPor);
        ejecucion.setTotalRecibidos(empleados == null ? 0 : empleados.size());
        ejecucion = ejecucionRepo.save(ejecucion);

        List<SyncEmpleadosCambioJpa> cambios = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (EmpleadoSyncDTO empleado : empleados == null ? List.<EmpleadoSyncDTO>of() : empleados) {
            procesarEmpleado(empleado, ejecucion, cambios, ahora);
        }

        cambioRepo.saveAll(cambios);
        ejecucionRepo.save(ejecucion);
        log.info("Sincronizacion de empleados ({}) por {}: {} recibidos, {} creados, {} actualizados, "
                + "{} inactivados, {} reactivados, {} sin cambios, {} advertencias",
                origen, ejecutadoPor, ejecucion.getTotalRecibidos(), ejecucion.getCreados(),
                ejecucion.getActualizados(), ejecucion.getInactivados(), ejecucion.getReactivados(),
                ejecucion.getSinCambios(), ejecucion.getAdvertencias());

        return toResultado(ejecucion, cambios.stream()
                .map(c -> new CambioSyncDTO(c.getCedula(), c.getTipo(), c.getDetalle(), c.getIdCustodio()))
                .toList());
    }

    private void procesarEmpleado(EmpleadoSyncDTO empleado, SyncEmpleadosEjecucionJpa ejecucion,
            List<SyncEmpleadosCambioJpa> cambios, LocalDateTime ahora) {

        String cedula = empleado.getCedula() == null ? "" : empleado.getCedula().trim();
        if (cedula.isBlank()) {
            ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
            cambios.add(cambio(ejecucion, "(sin cedula)", TIPO_ADVERTENCIA,
                    "Empleado sin cedula: omitido (" + Objects.toString(empleado.getNombre(), "sin nombre") + ")", null));
            return;
        }

        boolean activo = !Boolean.FALSE.equals(empleado.getActivo());
        Optional<CustodiosJpa> existente = custodiosRepo.findFirstByCedulaIgnoreCase(cedula);

        if (existente.isEmpty()) {
            if (!activo) {
                ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
                cambios.add(cambio(ejecucion, cedula, TIPO_ADVERTENCIA,
                        "Empleado inactivo en la fuente y sin registro local: omitido", null));
                return;
            }
            if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
                ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
                cambios.add(cambio(ejecucion, cedula, TIPO_ADVERTENCIA, "Empleado sin nombre: omitido", null));
                return;
            }
            CustodiosJpa nuevo = new CustodiosJpa();
            nuevo.setCedula(cedula);
            nuevo.setNombre(empleado.getNombre().trim());
            nuevo.setCorreo(vacioANull(empleado.getCorreo()));
            nuevo.setTelefono(vacioANull(empleado.getTelefono()));
            nuevo.setEstado(true);
            nuevo.setOrigenSync(true);
            nuevo.setSincronizadoEn(ahora);
            resolverCargo(empleado, ejecucion, cambios, cedula).ifPresent(nuevo::setFkCargo);
            CustodiosJpa guardado = custodiosRepo.save(nuevo);
            ejecucion.setCreados(ejecucion.getCreados() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_CREADO,
                    "Empleado registrado: " + guardado.getNombre(), guardado.getIdCustodio()));
            return;
        }

        CustodiosJpa custodio = existente.get();
        custodio.setSincronizadoEn(ahora);
        custodio.setOrigenSync(true);

        if (!activo && custodio.isEstado()) {
            custodio.setEstado(false);
            custodiosRepo.save(custodio);
            ejecucion.setInactivados(ejecucion.getInactivados() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_INACTIVADO,
                    "Empleado fuera de servicio segun la fuente", custodio.getIdCustodio()));

            long custodiasActivas = custodiasRepo.countByFkCustodio_IdCustodioAndEstadoTrue(custodio.getIdCustodio());
            if (custodiasActivas > 0) {
                ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
                cambios.add(cambio(ejecucion, cedula, TIPO_ALERTA_ACTIVOS,
                        "Empleado inactivo mantiene " + custodiasActivas + " custodia(s) activa(s): gestionar devolucion",
                        custodio.getIdCustodio()));
            }
            return;
        }

        List<String> camposCambiados = new ArrayList<>();
        if (activo && !custodio.isEstado()) {
            custodio.setEstado(true);
            custodiosRepo.save(custodio);
            ejecucion.setReactivados(ejecucion.getReactivados() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_REACTIVADO,
                    "Empleado reincorporado segun la fuente", custodio.getIdCustodio()));
            // sigue evaluando actualizaciones de datos sobre el mismo registro
        }

        if (empleado.getNombre() != null && !empleado.getNombre().isBlank()
                && !empleado.getNombre().trim().equals(custodio.getNombre())) {
            custodio.setNombre(empleado.getNombre().trim());
            camposCambiados.add("nombre");
        }
        String correo = vacioANull(empleado.getCorreo());
        if (correo != null && !correo.equalsIgnoreCase(Objects.toString(custodio.getCorreo(), ""))) {
            custodio.setCorreo(correo);
            camposCambiados.add("correo");
        }
        String telefono = vacioANull(empleado.getTelefono());
        if (telefono != null && !telefono.equals(custodio.getTelefono())) {
            custodio.setTelefono(telefono);
            camposCambiados.add("telefono");
        }
        Optional<CargosJpa> cargoResuelto = resolverCargo(empleado, ejecucion, cambios, cedula);
        if (cargoResuelto.isPresent()
                && (custodio.getFkCargo() == null
                        || custodio.getFkCargo().getIdCargo() != cargoResuelto.get().getIdCargo())) {
            custodio.setFkCargo(cargoResuelto.get());
            camposCambiados.add("cargo");
        }

        custodiosRepo.save(custodio);
        if (!camposCambiados.isEmpty()) {
            ejecucion.setActualizados(ejecucion.getActualizados() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_ACTUALIZADO,
                    "Campos actualizados: " + String.join(", ", camposCambiados), custodio.getIdCustodio()));
        } else if (custodio.isEstado()) {
            ejecucion.setSinCambios(ejecucion.getSinCambios() + 1);
        }
    }

    /**
     * Resuelve el cargo por nombre ignorando mayusculas y tildes. Si el JSON
     * trae departamento y no coincide con el del cargo, deja una advertencia.
     */
    private Optional<CargosJpa> resolverCargo(EmpleadoSyncDTO empleado, SyncEmpleadosEjecucionJpa ejecucion,
            List<SyncEmpleadosCambioJpa> cambios, String cedula) {
        String nombreCargo = vacioANull(empleado.getCargo());
        if (nombreCargo == null) {
            return Optional.empty();
        }
        Optional<CargosJpa> cargo = cargosRepo.findAll().stream()
                .filter(c -> normalizar(c.getNombre()).equals(normalizar(nombreCargo)))
                .findFirst();
        if (cargo.isEmpty()) {
            ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_ADVERTENCIA,
                    "Cargo \"" + nombreCargo + "\" no existe en CRESIO: se conserva el actual", null));
            return Optional.empty();
        }
        String departamento = vacioANull(empleado.getDepartamento());
        if (departamento != null && cargo.get().getFkDepartamento() != null
                && !normalizar(cargo.get().getFkDepartamento().getNombre()).equals(normalizar(departamento))) {
            ejecucion.setAdvertencias(ejecucion.getAdvertencias() + 1);
            cambios.add(cambio(ejecucion, cedula, TIPO_ADVERTENCIA,
                    "Departamento de la fuente (" + departamento + ") no coincide con el del cargo ("
                            + cargo.get().getFkDepartamento().getNombre() + ")", null));
        }
        return cargo;
    }

    @Transactional(readOnly = true)
    public EstadoSincronizacionDTO obtenerEstado() {
        EstadoSincronizacionDTO estado = new EstadoSincronizacionDTO();
        estado.setFuenteConfigurada(fuenteConfigurada());
        estado.setFuenteDescripcion(describirFuente());

        ejecucionRepo.findFirstByOrderByEjecutadoEnDesc().ifPresent(ejecucion -> {
            List<CambioSyncDTO> cambios = cambioRepo
                    .findByEjecucion_IdEjecucionOrderByIdCambioAsc(ejecucion.getIdEjecucion())
                    .stream()
                    .map(c -> new CambioSyncDTO(c.getCedula(), c.getTipo(), c.getDetalle(), c.getIdCustodio()))
                    .toList();
            estado.setUltimaEjecucion(toResultado(ejecucion, cambios));
        });

        estado.setAlertas(custodiasRepo.findByEstadoTrueAndFkCustodio_EstadoFalse().stream()
                .map(this::toAlerta)
                .toList());
        return estado;
    }

    private AlertaCustodioInactivoDTO toAlerta(CustodiasJpa custodia) {
        return new AlertaCustodioInactivoDTO(
                custodia.getFkCustodio() != null ? custodia.getFkCustodio().getIdCustodio() : null,
                custodia.getFkCustodio() != null ? custodia.getFkCustodio().getNombre() : null,
                custodia.getFkCustodio() != null ? custodia.getFkCustodio().getCedula() : null,
                custodia.getFkEquipo() != null ? custodia.getFkEquipo().getIdEquipo() : null,
                custodia.getFkEquipo() != null ? custodia.getFkEquipo().getCodigoCresio() : null,
                custodia.getFkEquipo() != null ? custodia.getFkEquipo().getModelo() : null);
    }

    private SincronizacionResultadoDTO toResultado(SyncEmpleadosEjecucionJpa ejecucion, List<CambioSyncDTO> cambios) {
        SincronizacionResultadoDTO dto = new SincronizacionResultadoDTO();
        dto.setIdEjecucion(ejecucion.getIdEjecucion());
        dto.setEjecutadoEn(ejecucion.getEjecutadoEn());
        dto.setOrigen(ejecucion.getOrigen());
        dto.setEjecutadoPor(ejecucion.getEjecutadoPor());
        dto.setTotalRecibidos(ejecucion.getTotalRecibidos());
        dto.setCreados(ejecucion.getCreados());
        dto.setActualizados(ejecucion.getActualizados());
        dto.setInactivados(ejecucion.getInactivados());
        dto.setReactivados(ejecucion.getReactivados());
        dto.setSinCambios(ejecucion.getSinCambios());
        dto.setAdvertencias(ejecucion.getAdvertencias());
        dto.setCambios(cambios);
        return dto;
    }

    private SyncEmpleadosCambioJpa cambio(SyncEmpleadosEjecucionJpa ejecucion, String cedula, String tipo,
            String detalle, Integer idCustodio) {
        SyncEmpleadosCambioJpa c = new SyncEmpleadosCambioJpa();
        c.setEjecucion(ejecucion);
        c.setCedula(cedula);
        c.setTipo(tipo);
        c.setDetalle(detalle);
        c.setIdCustodio(idCustodio);
        return c;
    }

    private static String vacioANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    /** Comparacion robusta: ignora mayusculas/minusculas y tildes. */
    static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
