package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.CandidatoDirectorioDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.CustodioResueltoDTO;
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
 * - Cargos se vinculan por nombre (sin tildes/mayusculas) solo si ya existen
 *   en CRESIO; no se crean ni se validan cargos/departamentos, no son datos
 *   necesarios para el historial de custodias.
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
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Pattern CSRF_TOKEN_PATTERN = Pattern
            .compile("name=[\"']csrf_token[\"'][^>]*value=[\"']([^\"']+)[\"']");

    @Value("${empleados.sync.url:}")
    private String fuenteUrl;

    @Value("${empleados.sync.archivo:}")
    private String fuenteArchivo;

    @Value("${empleados.sync.usuario:}")
    private String fuenteUsuario;

    @Value("${empleados.sync.contrasena:}")
    private String fuenteContrasena;

    /** Cookie de sesion de la fuente URL, cacheada entre llamadas mientras siga valida. */
    private volatile String cookieSesionFuente;

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
        String[] origenYJson = leerFuenteConfigurada();
        return sincronizar(parsearEmpleados(origenYJson[1]), origenYJson[0], ejecutadoPor);
    }

    /** Lee la fuente configurada (URL o archivo) sin sincronizar. Devuelve {origen, json}. */
    private String[] leerFuenteConfigurada() {
        if (!fuenteUrl.isBlank()) {
            return new String[] { "URL", leerUrlConSesion() };
        }
        if (!fuenteArchivo.isBlank()) {
            try {
                String json = Files.readString(Path.of(fuenteArchivo), StandardCharsets.UTF_8);
                return new String[] { "ARCHIVO", json };
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el archivo de empleados: " + e.getMessage(), e);
            }
        }
        throw new IllegalStateException(
                "No hay fuente de empleados configurada. Defina EMPLEADOS_SYNC_URL o EMPLEADOS_SYNC_ARCHIVO.");
    }

    /**
     * Lee la URL configurada. Si no requiere login (usuario/clave vacios) hace
     * una llamada anonima simple. Si requiere login, reutiliza la cookie de
     * sesion cacheada y, si la fuente la rechaza (sesion vencida), vuelve a
     * iniciar sesion una vez y reintenta.
     */
    private String leerUrlConSesion() {
        if (fuenteUsuario.isBlank() || fuenteContrasena.isBlank()) {
            return RestClient.create().get().uri(fuenteUrl).retrieve().body(String.class);
        }

        String cookie = cookieSesionFuente != null ? cookieSesionFuente : iniciarSesionYObtenerCookie();
        String cuerpo = obtenerConCookie(cookie);
        if (pareceHtmlDeLogin(cuerpo)) {
            cookie = iniciarSesionYObtenerCookie();
            cuerpo = obtenerConCookie(cookie);
            if (pareceHtmlDeLogin(cuerpo)) {
                throw new IllegalStateException(
                        "La fuente sigue exigiendo login tras reautenticar. Verifique EMPLEADOS_SYNC_USUARIO/EMPLEADOS_SYNC_CONTRASENA.");
            }
        }
        return cuerpo;
    }

    private String obtenerConCookie(String cookie) {
        return RestClient.create().get().uri(fuenteUrl)
                .header(HttpHeaders.COOKIE, cookie)
                .retrieve()
                .body(String.class);
    }

    private static boolean pareceHtmlDeLogin(String cuerpo) {
        return cuerpo == null || cuerpo.stripLeading().startsWith("<");
    }

    /** Cliente sin seguimiento automatico de redirecciones, para poder leer el Set-Cookie del 302 de login. */
    private static RestClient clienteSinRedireccion() {
        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
        return RestClient.builder().requestFactory(new JdkClientHttpRequestFactory(httpClient)).build();
    }

    private synchronized String iniciarSesionYObtenerCookie() {
        String origen = origenDeFuente();
        RestClient cliente = clienteSinRedireccion();

        ResponseEntity<String> paginaLogin = cliente.get().uri(origen + "/login").retrieve().toEntity(String.class);
        String csrfToken = extraerCsrfToken(paginaLogin.getBody());
        String cookiePagina = primeraCookie(paginaLogin.getHeaders());

        MultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("csrf_token", csrfToken);
        formulario.add("username", fuenteUsuario);
        formulario.add("password", fuenteContrasena);
        formulario.add("user_id", "");
        formulario.add("acepta_terminos", "false");

        RestClient.RequestBodySpec peticion = cliente.post().uri(origen + "/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (cookiePagina != null) {
            peticion = (RestClient.RequestBodySpec) peticion.header(HttpHeaders.COOKIE, cookiePagina);
        }
        ResponseEntity<String> respuestaLogin = peticion.body(formulario).retrieve().toEntity(String.class);

        String cookieSesion = primeraCookie(respuestaLogin.getHeaders());
        if (cookieSesion == null) {
            throw new IllegalStateException(
                    "El login contra el directorio no devolvio cookie de sesion (usuario o clave invalidos?)");
        }
        this.cookieSesionFuente = cookieSesion;
        return cookieSesion;
    }

    private String origenDeFuente() {
        URI uri = URI.create(fuenteUrl);
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    private static String extraerCsrfToken(String html) {
        if (html == null) {
            return "";
        }
        Matcher m = CSRF_TOKEN_PATTERN.matcher(html);
        return m.find() ? m.group(1) : "";
    }

    /** Junta todas las cookies devueltas (Set-Cookie puede venir en varias lineas) en un solo header Cookie. */
    private static String primeraCookie(HttpHeaders headers) {
        List<String> setCookie = headers.get(HttpHeaders.SET_COOKIE);
        if (setCookie == null || setCookie.isEmpty()) {
            return null;
        }
        List<String> pares = setCookie.stream().map(c -> c.split(";", 2)[0]).toList();
        return String.join("; ", pares);
    }

    /**
     * Busca en vivo en la fuente configurada (sin persistir nada) candidatos
     * cuyo nombre o cedula coincidan con q. Usado por el autocompletar de
     * asignaciones para encontrar personas que aun no son custodios locales.
     */
    public List<CandidatoDirectorioDTO> buscarEnDirectorio(String q) {
        List<EmpleadoSyncDTO> empleados = parsearEmpleados(leerFuenteConfigurada()[1]);
        String busqueda = normalizar(q);
        return empleados.stream()
                .filter(e -> e.getCedula() != null && !e.getCedula().isBlank())
                .filter(e -> busqueda.isBlank()
                        || normalizar(e.getNombre()).contains(busqueda)
                        || e.getCedula().contains(busqueda))
                .limit(12)
                .map(e -> new CandidatoDirectorioDTO(e.getCedula().trim(), e.getNombre(), e.getCargo(),
                        e.getDepartamento()))
                .toList();
    }

    /**
     * Ubica en la fuente configurada a la persona con esa cedula y crea o
     * actualiza su custodio local (misma resolucion de cargo que usa el lote).
     * Se usa al asignar un activo a alguien que todavia no tiene registro local.
     */
    @Transactional
    public CustodioResueltoDTO resolverDesdeDirectorio(String cedula) {
        String cedulaBuscada = cedula == null ? "" : cedula.trim();
        if (cedulaBuscada.isBlank()) {
            throw new IllegalArgumentException("Cedula requerida");
        }

        List<EmpleadoSyncDTO> empleados = parsearEmpleados(leerFuenteConfigurada()[1]);
        EmpleadoSyncDTO empleado = empleados.stream()
                .filter(e -> cedulaBuscada.equalsIgnoreCase(Objects.toString(e.getCedula(), "").trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La persona ya no aparece en el directorio institucional"));

        if (Boolean.FALSE.equals(empleado.getActivo())) {
            throw new IllegalArgumentException(
                    "No se puede asignar: el empleado figura inactivo en el directorio institucional");
        }
        if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
            throw new IllegalArgumentException("El directorio no tiene un nombre valido para esta persona");
        }

        Optional<CustodiosJpa> existente = custodiosRepo.findFirstByCedulaIgnoreCase(cedulaBuscada);
        boolean esNuevo = existente.isEmpty();
        CustodiosJpa custodio = existente.orElseGet(CustodiosJpa::new);

        LocalDateTime ahora = LocalDateTime.now();
        custodio.setCedula(cedulaBuscada);
        custodio.setNombre(empleado.getNombre().trim());
        if (vacioANull(empleado.getCorreo()) != null) {
            custodio.setCorreo(vacioANull(empleado.getCorreo()));
        }
        if (vacioANull(empleado.getTelefono()) != null) {
            custodio.setTelefono(vacioANull(empleado.getTelefono()));
        }
        custodio.setEstado(true);
        custodio.setOrigenSync(true);
        custodio.setSincronizadoEn(ahora);
        custodio.setCargoDirectorio(vacioANull(empleado.getCargo()));
        custodio.setDepartamentoDirectorio(vacioANull(empleado.getDepartamento()));
        resolverCargo(empleado).ifPresent(custodio::setFkCargo);

        CustodiosJpa guardado = custodiosRepo.save(custodio);

        return new CustodioResueltoDTO(guardado.getIdCustodio(), guardado.getNombre(), guardado.getCedula(),
                guardado.getCargoDirectorio(), guardado.getDepartamentoDirectorio(), esNuevo, List.of());
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
            nuevo.setCargoDirectorio(vacioANull(empleado.getCargo()));
            nuevo.setDepartamentoDirectorio(vacioANull(empleado.getDepartamento()));
            resolverCargo(empleado).ifPresent(nuevo::setFkCargo);
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
        String cargoDirectorio = vacioANull(empleado.getCargo());
        if (cargoDirectorio != null && !cargoDirectorio.equals(custodio.getCargoDirectorio())) {
            custodio.setCargoDirectorio(cargoDirectorio);
            camposCambiados.add("cargo");
        }
        String departamentoDirectorio = vacioANull(empleado.getDepartamento());
        if (departamentoDirectorio != null && !departamentoDirectorio.equals(custodio.getDepartamentoDirectorio())) {
            custodio.setDepartamentoDirectorio(departamentoDirectorio);
            camposCambiados.add("departamento");
        }
        Optional<CargosJpa> cargoResuelto = resolverCargo(empleado);
        if (cargoResuelto.isPresent()
                && (custodio.getFkCargo() == null
                        || custodio.getFkCargo().getIdCargo() != cargoResuelto.get().getIdCargo())) {
            custodio.setFkCargo(cargoResuelto.get());
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
     * Vincula el cargo si ya existe en CRESIO con ese nombre (sin tildes ni
     * mayusculas). Cargos y departamentos no se crean ni se validan: no son
     * datos necesarios para el historial de custodias, asi que si no hay un
     * cargo local que coincida, el custodio se guarda igual, sin cargo.
     */
    private Optional<CargosJpa> resolverCargo(EmpleadoSyncDTO empleado) {
        String nombreCargo = vacioANull(empleado.getCargo());
        if (nombreCargo == null) {
            return Optional.empty();
        }
        return cargosRepo.findAll().stream()
                .filter(c -> normalizar(c.getNombre()).equals(normalizar(nombreCargo)))
                .findFirst();
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
