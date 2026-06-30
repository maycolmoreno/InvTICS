package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoProgramadoResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.exception.BackendException;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoControlador {

    private final IMantenimientoManualServicio mantenimientoManualServicio;
    private final IMantenimientoProgramadoServicio mantenimientoProgramadoServicio;
    private final IActividadChecklistServicio actividadChecklistServicio;
    private final IEquiposServicio equiposServicio;
    private final ICustodiasServicio custodiasServicio;
    private final ICustodiosServicio custodiosServicio;
    private final IUsuariosServicio usuariosServicio;
    private final IUbicacionesServicio ubicacionesServicio;
    private final IInventarioOperacionServicio inventarioOperacionServicio;
    private final SesionUsuario sesionUsuario;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginaResponse<MantenimientoManualResponseDTO> pagina;

        if (sesionUsuario.tieneRol("TECNICO") && sesionUsuario.getIdUsuario() != null) {
            pagina = mantenimientoManualServicio.listarPorTecnicoPaginado(sesionUsuario.getIdUsuario(), page, size);
        } else {
            pagina = mantenimientoManualServicio.listarTodosPaginado(page, size);
        }

        model.addAttribute("listamantenimientos", pagina.getContenido());
        model.addAttribute("paginaActual", pagina.getPaginaActual());
        model.addAttribute("totalPaginas", pagina.getTotalPaginas());
        model.addAttribute("totalElementos", pagina.getTotalElementos());
        model.addAttribute("tamanioPagina", pagina.getTamanioPagina());

        List<com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO> ubicaciones =
                ubicacionesServicio.listarUbicaciones();
        model.addAttribute("listaubicaciones", ubicaciones);

        List<EquiposResponseDTO> equipos = equiposServicio.listarEquipos();
        model.addAttribute("listaequipos", equipos);

        List<CustodiosResponseDTO> custodios = custodiosServicio.listarCustodios();
        model.addAttribute("listacustodios", custodios);
        model.addAttribute("custodiosActivos", custodios.stream().filter(CustodiosResponseDTO::isEstado).toList());

        List<CustodiasResponseDTO> custodiasActivas = custodiasServicio.listarCustodias().stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> idCustodio(c) != null && c.getFkEquipo() != null)
                .toList();
        Map<Integer, String> custodiosPorEquipo = custodiasActivas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFkEquipo().getIdEquipo(),
                        LinkedHashMap::new,
                        Collectors.mapping(c -> String.valueOf(idCustodio(c)),
                                Collectors.collectingAndThen(Collectors.toList(), ids -> String.join(",", ids)))));
        model.addAttribute("custodiosPorEquipo", custodiosPorEquipo);

        model.addAttribute("listausuarios", usuariosServicio.listarUsuario().stream()
                .filter(com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO::isEstado).toList());
        model.addAttribute("listaprogramados", mantenimientoProgramadoServicio.listarTodos());
        model.addAttribute("vencidosProximos", mantenimientoProgramadoServicio.listarVencidosYProximos());

        try {
            model.addAttribute("activosEnReparacion", inventarioOperacionServicio.listarActivosEnReparacion());
        } catch (Exception ex) {
            model.addAttribute("activosEnReparacion", List.of());
        }
        try {
            model.addAttribute("activosEnBodega", inventarioOperacionServicio.listarActivosEnBodega());
        } catch (Exception ex) {
            model.addAttribute("activosEnBodega", List.of());
        }
        try {
            model.addAttribute("bodegas", inventarioOperacionServicio.listarBodegas());
        } catch (Exception ex) {
            model.addAttribute("bodegas", List.of());
        }
        model.addAttribute("enviarReparacionRequest", new EnviarReparacionRequestDTO());
        model.addAttribute("retornarReparacionRequest", new RetornarReparacionRequestDTO());

        return "mantenimiento/lista-mantenimientos";
    }

    @PostMapping("/reparaciones/enviar")
    public String enviarReparacion(
            @ModelAttribute EnviarReparacionRequestDTO request,
            @RequestParam(required = false) String modalidad,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) Integer tecnicoId,
            @RequestParam(required = false) LocalDate fechaEstimadaRetorno,
            @RequestParam(required = false) Integer custodioIdForm,
            RedirectAttributes redirect) {

        // Resolver custodioId: primero del formulario, luego de la custodia activa del equipo
        Integer custodioId = custodioIdForm;
        if (custodioId == null && request.getEquipoId() != null) {
            try {
                custodioId = custodiasServicio.listarCustodias().stream()
                        .filter(CustodiasResponseDTO::isEstado)
                        .filter(c -> c.getFkEquipo() != null
                                && request.getEquipoId().equals(c.getFkEquipo().getIdEquipo()))
                        .map(this::idCustodio)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);
            } catch (Exception ex) {
                log.warn("No se pudo resolver custodioId para equipo {}: {}", request.getEquipoId(), ex.getMessage());
            }
        }

        // Resolver nombre del técnico para firmaTecnico
        String firmaTecnico = null;
        if (tecnicoId != null) {
            try {
                firmaTecnico = usuariosServicio.listarUsuario().stream()
                        .filter(u -> tecnicoId.equals(u.getIdUsuario()))
                        .map(com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO::getNombre)
                        .findFirst()
                        .orElse(null);
            } catch (Exception ex) {
                log.warn("No se pudo resolver nombre de tecnico {}: {}", tecnicoId, ex.getMessage());
            }
        }

        // Crear OT vinculada si tenemos custodioId
        if (custodioId != null && request.getEquipoId() != null) {
            try {
                MantenimientoManualRequestDTO otDto = new MantenimientoManualRequestDTO();
                otDto.setEquipoIds(List.of(request.getEquipoId()));
                otDto.setCustodioId(custodioId);
                otDto.setTipoMantenimiento("CORRECTIVO");
                otDto.setFechaMantenimiento(
                        request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDate.now());
                otDto.setProximaFecha(fechaEstimadaRetorno);
                otDto.setFirmaTecnico(firmaTecnico);
                otDto.setEstadoGeneral("EN_PROCESO");

                StringBuilder detalle = new StringBuilder(
                        request.getMotivo() != null ? request.getMotivo() : "Envio a reparacion");
                if (modalidad != null && !modalidad.isBlank())
                    detalle.append(" | Modalidad: ").append(modalidad);
                if (request.getProveedorTecnico() != null && !request.getProveedorTecnico().isBlank())
                    detalle.append(" | Taller: ").append(request.getProveedorTecnico());
                if (prioridad != null && !prioridad.isBlank())
                    detalle.append(" | Prioridad: ").append(prioridad);
                if (request.getObservacion() != null && !request.getObservacion().isBlank())
                    detalle.append(" | ").append(request.getObservacion());
                otDto.setDetalle(detalle.toString());

                mantenimientoManualServicio.crear(otDto);
                redirect.addFlashAttribute("otCreada", true);
            } catch (Exception ex) {
                log.warn("OT no pudo crearse para equipo {}: {}", request.getEquipoId(), ex.getMessage());
                redirect.addFlashAttribute("otWarning", "El activo fue enviado a reparacion pero la OT no pudo registrarse: " + ex.getMessage());
            }
        } else if (custodioId == null) {
            redirect.addFlashAttribute("otWarning",
                    "El activo fue enviado a reparacion sin OT vinculada (no se encontro custodio responsable).");
        }

        // Ejecutar el cambio de estado del activo (siempre)
        try {
            var activo = inventarioOperacionServicio.enviarAReparacion(request);
            redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " enviado a reparacion.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se pudo enviar a reparacion: " + ex.getMessage());
        }
        return "redirect:/mantenimiento";
    }

    @PostMapping("/reparaciones/retornar")
    public String retornarReparacion(
            @ModelAttribute RetornarReparacionRequestDTO request,
            @RequestParam(required = false) String resultadoTecnico,
            @RequestParam(required = false) String costoReal,
            RedirectAttributes redirect) {

        // 1. Cambiar estado del activo (siempre primero)
        String codigoActivo = null;
        try {
            var activo = inventarioOperacionServicio.retornarDeReparacion(request);
            codigoActivo = activo.getCodigoCresio();
            redirect.addFlashAttribute("success", "Activo " + codigoActivo + " retornado a bodega.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se pudo retornar de reparacion: " + ex.getMessage());
            return "redirect:/mantenimiento";
        }

        // 2. Cerrar OT activa vinculada al equipo
        if (request.getEquipoId() != null) {
            try {
                List<MantenimientoManualResponseDTO> historial =
                        mantenimientoManualServicio.obtenerHistorial(request.getEquipoId());

                historial.stream()
                        .filter(ot -> "EN_PROCESO".equals(ot.getEstadoInterno())
                                || "PENDIENTE".equals(ot.getEstadoInterno()))
                        .max(java.util.Comparator.comparing(
                                ot -> ot.getCreadoEn() != null ? ot.getCreadoEn()
                                        : java.time.LocalDateTime.MIN))
                        .ifPresent(ot -> {
                            try {
                                mantenimientoManualServicio.cerrar(ot.getIdMantenimiento(), resultadoTecnico, null);
                                redirect.addFlashAttribute("otCerrada", true);
                            } catch (Exception ex) {
                                log.warn("No se pudo cerrar OT {} al retornar equipo {}: {}",
                                        ot.getIdMantenimiento(), request.getEquipoId(), ex.getMessage());
                                redirect.addFlashAttribute("otWarning",
                                        "El activo fue retornado pero la OT no pudo cerrarse automaticamente.");
                            }
                        });
            } catch (Exception ex) {
                log.warn("No se pudo obtener historial de OT para equipo {}: {}",
                        request.getEquipoId(), ex.getMessage());
            }
        }

        // 3. Banner de baja si el resultado lo requiere
        if ("IRREPARABLE".equals(resultadoTecnico) || "REQUIERE_BAJA".equals(resultadoTecnico)) {
            redirect.addFlashAttribute("requiereBaja", true);
            redirect.addFlashAttribute("codigoActivoBaja", codigoActivo);
        }

        return "redirect:/mantenimiento";
    }

    @GetMapping("/kanban")
    public String kanban() {
        return "redirect:/mantenimiento?view=kanban";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer equipoId,
            @RequestParam(required = false) String equipoIds,
            Model model) {
        List<Integer> preseleccion = new ArrayList<>();
        if (equipoId != null) {
            preseleccion.add(equipoId);
        }
        if (equipoIds != null && !equipoIds.isBlank()) {
            preseleccion.addAll(List.of(equipoIds.split(",")).stream()
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(Integer::valueOf)
                    .toList());
        }
        List<ActividadManualRequestDTO> actividades = actividadesBase();
        List<CustodiasResponseDTO> custodiasActivas = custodiasServicio.listarCustodias().stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> idCustodio(c) != null && c.getFkEquipo() != null)
                .toList();
        Map<Integer, String> custodiosPorEquipo = custodiasActivas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFkEquipo().getIdEquipo(),
                        LinkedHashMap::new,
                        Collectors.mapping(c -> String.valueOf(idCustodio(c)),
                                Collectors.collectingAndThen(Collectors.toList(), ids -> String.join(",", ids)))));

        List<CustodiosResponseDTO> custodiosActivos = custodiosServicio.listarCustodios().stream()
                .filter(CustodiosResponseDTO::isEstado).toList();
        List<CustodiosResponseDTO> custodiosAdmin = custodiosActivos.stream()
                .filter(c -> c.getFkUbicacion() == null).toList();
        boolean custodiosAdminFallback = custodiosAdmin.isEmpty();
        if (custodiosAdminFallback) {
            custodiosAdmin = custodiosActivos;
        }
        List<CustodiosResponseDTO> custodiosFarmacia = custodiosAdminFallback
                ? List.of()
                : custodiosActivos.stream().filter(c -> c.getFkUbicacion() != null).toList();

        model.addAttribute("listaequipos", equiposServicio.listarEquipos());
        model.addAttribute("custodiosAdmin", custodiosAdmin);
        model.addAttribute("custodiosAdminFallback", custodiosAdminFallback);
        model.addAttribute("custodiosFarmacia", custodiosFarmacia);
        model.addAttribute("listaubicaciones", ubicacionesServicio.listarUbicaciones().stream().filter(UbicacionesResponseDTO::isEstado).toList());
        model.addAttribute("actividades", actividades);
        model.addAttribute("totalActividades", actividades.size());
        model.addAttribute("equiposPreseleccionados", preseleccion);
        model.addAttribute("custodiosPorEquipo", custodiosPorEquipo);
        model.addAttribute("hoy", LocalDate.now());
        return "mantenimiento/registro-manual";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam List<Integer> equipoIds,
            @RequestParam Integer custodioId,
            @RequestParam(defaultValue = "ADMINISTRATIVO") String modoSeleccion,
            @RequestParam String tipoMantenimiento,
            @RequestParam LocalDate fechaMantenimiento,
            @RequestParam(required = false) LocalDate proximaFecha,
            @RequestParam String estadoGeneral,
            @RequestParam String detalle,
            @RequestParam(required = false) String firmaTecnico,
            @RequestParam(required = false) String firmaCustodio,
            @RequestParam(name = "actividadIds") List<Integer> actividadIds,

            @RequestParam(name = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam Map<String, String> requestParams,
            RedirectAttributes redirectAttributes) {

        int totalActividades = actividadChecklistServicio.listarActivas().size();
        if (actividadIds == null || actividadIds.size() < totalActividades) {
            redirectAttributes.addFlashAttribute("error", "Debes completar todo el checklist");
            return "redirect:/mantenimiento/nuevo";
        }

        List<Integer> ids = equipoIds.stream().distinct().toList();
        boolean modoAdministrativo = "ADMINISTRATIVO".equalsIgnoreCase(modoSeleccion);
        if (modoAdministrativo && !equiposPertenecenACustodio(ids, custodioId)) {
            redirectAttributes.addFlashAttribute("error",
                    "Uno o mas equipos seleccionados no estan asignados al custodio indicado.");
            return "redirect:/mantenimiento/nuevo";
        }

        String detalleCompleto = construirDetalleConObservaciones(detalle, requestParams);

        MantenimientoManualRequestDTO request = new MantenimientoManualRequestDTO();
        request.setEquipoIds(ids);
        request.setCustodioId(custodioId);
        request.setTipoMantenimiento(tipoMantenimiento);
        request.setFechaMantenimiento(fechaMantenimiento);
        request.setProximaFecha(proximaFecha);
        request.setEstadoGeneral(estadoGeneral);
        request.setDetalle(detalleCompleto);
        request.setFirmaTecnico(firmaTecnico);
        request.setFirmaCustodio(firmaCustodio);
        request.setActividades(construirActividadesSeleccionadas(actividadIds));

        MantenimientoManualResponseDTO creado;
        try {
            creado = mantenimientoManualServicio.crear(request);
        } catch (BackendException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/mantenimiento/nuevo";
        }
        creado.setTipoMantenimiento(tipoMantenimiento);
        List<ImagenMantenimientoRequestDTO> metadata = mantenimientoManualServicio
                .subirImagenes(creado.getIdMantenimiento(), imagenes);
        if (!metadata.isEmpty()) {
            creado.setImagenes(metadata.stream().map(img -> {
                var dto = new com.uisrael.consumogestionactivosapi.modelo.dto.response.ImagenMantenimientoResponseDTO();
                dto.setNombreArchivo(img.getNombreArchivo());
                dto.setRutaArchivo(img.getRutaArchivo());
                dto.setTamanioBytes(img.getTamanioBytes());
                return dto;
            }).toList());
        }

        redirectAttributes.addFlashAttribute("exito", "Orden de mantenimiento guardada correctamente");
        return "redirect:/mantenimiento";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        try {
            var dto = mantenimientoManualServicio.obtenerDetalle(id);
            // Asegurar listas no-null para evitar NPE en Thymeleaf
            if (dto.getActividades() == null) {
                dto.setActividades(new java.util.ArrayList<>());
            }
            if (dto.getImagenes() == null) {
                dto.setImagenes(new java.util.ArrayList<>());
            }
            if (dto.getEquipos() == null) {
                dto.setEquipos(new java.util.ArrayList<>());
            }
            model.addAttribute("mantenimiento", dto);
        } catch (Exception e) {
            log.error("Error al cargar detalle mantenimiento {}: {}", id, e.getMessage());
            return "redirect:/mantenimiento";
        }
        return "mantenimiento/detalle-mantenimiento";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> verPdf(@PathVariable Integer id) {
        byte[] pdfBytes = mantenimientoManualServicio.descargarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"mantenimiento_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{id}/imagen/{filename:.+}")
    public ResponseEntity<byte[]> servirImagen(@PathVariable Integer id, @PathVariable String filename) {
        // Validar filename contra path traversal
        if (filename == null || !filename.matches("[a-zA-Z0-9._-]+")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] bytes = mantenimientoManualServicio.obtenerImagen(id, filename);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        String mime = filename.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mime)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(bytes);
    }

    @PostMapping("/{id}/reenviar-correo")
    public String reenviarCorreo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            mantenimientoManualServicio.reenviarCorreo(id);
            redirectAttributes.addFlashAttribute("exito", "Correo reenviado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo reenviar el correo: " + e.getMessage());
        }
        return "redirect:/mantenimiento/" + id;
    }

    @GetMapping("/historial/{equipoId}")
    public String historial(@PathVariable Integer equipoId) {
        return "redirect:/activos/equipos/" + equipoId + "/expediente#tab-mantenimientos";
    }

    @PostMapping("/cerrar/{id}")
    public String cerrar(@PathVariable Integer id,
                         @RequestParam(required = false) String resultadoTecnico,
                         @RequestParam(required = false) String observacionCierre,
                         RedirectAttributes redirectAttributes) {
        mantenimientoManualServicio.cerrar(id, resultadoTecnico, observacionCierre);
        String msg = "Orden cerrada correctamente.";
        if (resultadoTecnico != null && !resultadoTecnico.isBlank()) {
            msg += " Resultado: " + resultadoTecnico;
        }
        redirectAttributes.addFlashAttribute("exito", msg);
        return "redirect:/mantenimiento/" + id;
    }

    @GetMapping("/programado")
    public String programado() {
        return "redirect:/mantenimiento?view=programados";
    }

    @PostMapping("/programado/guardar")
    public String guardarProgramado(MantenimientoProgramadoRequestDTO request, RedirectAttributes redirectAttributes) {
        mantenimientoProgramadoServicio.guardar(request);
        redirectAttributes.addFlashAttribute("exito", "Programacion guardada correctamente");
        return "redirect:/mantenimiento";
    }

    @PostMapping("/programado/desactivar")
    public String desactivarProgramado(@RequestParam Long idProgramado, RedirectAttributes redirectAttributes) {
        mantenimientoProgramadoServicio.desactivar(idProgramado);
        redirectAttributes.addFlashAttribute("exito", "Programacion desactivada correctamente");
        return "redirect:/mantenimiento/programado";
    }

    private List<ActividadManualRequestDTO> actividadesBase() {
        return actividadChecklistServicio.listarActivas().stream()
                .sorted(java.util.Comparator.comparing(ActividadChecklistResponseDTO::getOrden, java.util.Comparator.nullsLast(Integer::compareTo)))
                .map(act -> {
                    ActividadManualRequestDTO dto = new ActividadManualRequestDTO();
                    dto.setIdActividad(act.getIdActividad());
                    dto.setNombreActividad(act.getNombre());
                    dto.setCategoriaActividad(act.getCategoria());
                    dto.setRealizada(Boolean.FALSE);
                    return dto;
                })
                .toList();
    }

    private List<ActividadManualRequestDTO> construirActividadesSeleccionadas(List<Integer> actividadIds) {
        return actividadChecklistServicio.listarActivas().stream().map(act -> {
            ActividadManualRequestDTO dto = new ActividadManualRequestDTO();
            dto.setIdActividad(act.getIdActividad());
            dto.setNombreActividad(act.getNombre());
            dto.setCategoriaActividad(act.getCategoria());
            dto.setRealizada(actividadIds.contains(act.getIdActividad()));
            return dto;
        }).toList();
    }

    private String construirDetalleConObservaciones(String detalleBase, Map<String, String> requestParams) {
        StringBuilder builder = new StringBuilder(Objects.toString(detalleBase, "").trim());
        String obsChecklist = Objects.toString(requestParams.get("observacionChecklist"), "").trim();

        if (!obsChecklist.isBlank()) {
            if (!builder.isEmpty()) {
                builder.append("\n\n");
            }
            builder.append("Observaciones checklist:\n").append(obsChecklist);
        }

        return builder.toString().trim();
    }

    private boolean equiposPertenecenACustodio(List<Integer> equipoIds, Integer custodioId) {
        if (custodioId == null || equipoIds == null || equipoIds.isEmpty()) {
            return false;
        }

        Set<Integer> equiposAsignados = custodiasServicio.listarCustodias().stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(custodia -> custodioCoincide(custodia, custodioId))
                .map(CustodiasResponseDTO::getFkEquipo)
                .filter(Objects::nonNull)
                .map(EquiposResponseDTO::getIdEquipo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return !equiposAsignados.isEmpty() && equiposAsignados.containsAll(equipoIds);
    }

    private Integer idCustodio(CustodiasResponseDTO custodia) {
        if (custodia == null) {
            return null;
        }
        if (custodia.getFkCustodio() != null) {
            return custodia.getFkCustodio().getIdCustodio();
        }
        return custodia.getIdCustodio() > 0 ? custodia.getIdCustodio() : null;
    }

    private boolean custodioCoincide(CustodiasResponseDTO custodia, Integer custodioId) {
        if (custodia == null || custodioId == null) {
            return false;
        }
        if (custodia.getFkCustodio() != null && Objects.equals(custodia.getFkCustodio().getIdCustodio(), custodioId)) {
            return true;
        }
        return Objects.equals(custodia.getIdCustodio(), custodioId);
    }
}
