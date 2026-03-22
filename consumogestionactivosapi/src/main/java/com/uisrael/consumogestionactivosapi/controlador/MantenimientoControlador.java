package com.uisrael.consumogestionactivosapi.controlador;

import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;
import com.uisrael.consumogestionactivosapi.service.CorreoServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.service.MantenimientoArchivoService;
import com.uisrael.consumogestionactivosapi.service.PdfMantenimientoService;

import lombok.RequiredArgsConstructor;

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
    private final MantenimientoArchivoService archivoService;
    private final PdfMantenimientoService pdfService;
    private final CorreoServicio correoServicio;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("listamantenimientos", mantenimientoManualServicio.listarTodos());
        model.addAttribute("listaequipos", equiposServicio.listarEquipos());
        model.addAttribute("listacustodios", custodiosServicio.listarCustodios());
        model.addAttribute("listaubicaciones", ubicacionesServicio.listarUbicaciones());
        return "mantenimiento/lista-mantenimientos";
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
        Map<String, List<ActividadManualRequestDTO>> actividadesAgrupadas = actividadesBase();
        List<CustodiasResponseDTO> custodiasActivas = custodiasServicio.listarCustodias().stream()
                .filter(CustodiasResponseDTO::isEstado)
                .filter(c -> c.getFkCustodio() != null && c.getFkEquipo() != null)
                .toList();
        Map<Integer, String> custodiosPorEquipo = custodiasActivas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFkEquipo().getIdEquipo(),
                        LinkedHashMap::new,
                        Collectors.mapping(c -> String.valueOf(c.getFkCustodio().getIdCustodio()),
                                Collectors.collectingAndThen(Collectors.toList(), ids -> String.join(",", ids)))));

        model.addAttribute("listaequipos", equiposServicio.listarEquipos());
        model.addAttribute("listacustodios", custodiosServicio.listarCustodios().stream().filter(CustodiosResponseDTO::isEstado).toList());
        model.addAttribute("actividadesAgrupadas", actividadesAgrupadas);
        model.addAttribute("totalActividades", actividadesAgrupadas.values().stream().mapToInt(List::size).sum());
        model.addAttribute("equiposPreseleccionados", preseleccion);
        model.addAttribute("custodiosPorEquipo", custodiosPorEquipo);
        model.addAttribute("hoy", LocalDate.now());
        return "mantenimiento/registro-manual";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam List<Integer> equipoIds,
            @RequestParam Integer custodioId,
            @RequestParam String tipoMantenimiento,
            @RequestParam LocalDate fechaMantenimiento,
            @RequestParam(required = false) LocalDate proximaFecha,
            @RequestParam String estadoGeneral,
            @RequestParam String detalle,
            @RequestParam String firmaTecnico,
            @RequestParam String firmaCustodio,
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
        if (!equiposPertenecenACustodio(ids, custodioId)) {
            redirectAttributes.addFlashAttribute("error",
                    "Uno o mas equipos seleccionados no estan asignados al custodio indicado.");
            return "redirect:/mantenimiento/nuevo";
        }

        String detalleCompleto = construirDetalleConObservaciones(detalle, requestParams);

        for (Integer idEquipo : ids) {
            MantenimientoManualRequestDTO request = new MantenimientoManualRequestDTO();
            request.setEquipoId(idEquipo);
            request.setCustodioId(custodioId);
            request.setTipoMantenimiento(tipoMantenimiento);
            request.setFechaMantenimiento(fechaMantenimiento);
            request.setProximaFecha(proximaFecha);
            request.setEstadoGeneral(estadoGeneral);
            request.setDetalle(detalleCompleto);
            request.setFirmaTecnico(firmaTecnico);
            request.setFirmaCustodio(firmaCustodio);
            request.setActividades(construirActividadesSeleccionadas(actividadIds));

            MantenimientoManualResponseDTO creado = mantenimientoManualServicio.crear(request);
            creado.setTipoMantenimiento(tipoMantenimiento);
            List<ImagenMantenimientoRequestDTO> metadata = archivoService.guardarImagenes(creado.getIdMantenimiento(), imagenes);
            if (!metadata.isEmpty()) {
                mantenimientoManualServicio.guardarImagenes(creado.getIdMantenimiento(), metadata);
                creado.setImagenes(metadata.stream().map(img -> {
                    var dto = new com.uisrael.consumogestionactivosapi.modelo.dto.response.ImagenMantenimientoResponseDTO();
                    dto.setNombreArchivo(img.getNombreArchivo());
                    dto.setRutaArchivo(img.getRutaArchivo());
                    dto.setTamanioBytes(img.getTamanioBytes());
                    return dto;
                }).toList());
            }

            completarDatosCustodio(creado, custodioId);
            CustodiosResponseDTO custodio = custodiosServicio.obtenerPorId(custodioId);
            EquiposResponseDTO equipo = equiposServicio.obtenerPorId(idEquipo);
            UsuariosResponseDTO tecnico = creado.getTecnicoId() == null ? null
                    : usuariosServicio.obtenerUsuario(creado.getTecnicoId());
            List<Path> rutasImagenes = metadata.stream().map(m -> Paths.get(m.getRutaArchivo())).toList();
            byte[] pdfBytes = pdfService.generarInforme(creado, equipo, custodio, tecnico, rutasImagenes);
            archivoService.guardarPdf(creado.getIdMantenimiento(), pdfBytes);
            correoServicio.enviarInformeMantenimientoConPdf(
                    correoDestino(creado, custodioId),
                    creado.getCustodioNombre(),
                    String.valueOf(creado.getIdMantenimiento()),
                    pdfBytes,
                    creado.getFechaMantenimiento(),
                    creado.getTipoMantenimiento(),
                    creado.getDetalle());
        }

        redirectAttributes.addFlashAttribute("exito", "Orden de mantenimiento guardada y reporte enviado");
        return "redirect:/mantenimiento";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("mantenimiento", mantenimientoManualServicio.obtenerDetalle(id));
        return "mantenimiento/detalle-mantenimiento";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> verPdf(@PathVariable Integer id) {
        byte[] pdfBytes = archivoService.leerPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"mantenimiento_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/reenviar-correo")
    public String reenviarCorreo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            MantenimientoManualResponseDTO mantenimiento = mantenimientoManualServicio.obtenerDetalle(id);
            if (mantenimiento == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontro el mantenimiento solicitado.");
                return "redirect:/mantenimiento";
            }

            completarDatosCustodio(mantenimiento, mantenimiento.getCustodioId());
            CustodiosResponseDTO custodio = mantenimiento.getCustodioId() == null ? null
                    : custodiosServicio.obtenerPorId(mantenimiento.getCustodioId());
            EquiposResponseDTO equipo = mantenimiento.getEquipoId() == null ? null
                    : equiposServicio.obtenerPorId(mantenimiento.getEquipoId());
            UsuariosResponseDTO tecnico = mantenimiento.getTecnicoId() == null ? null
                    : usuariosServicio.obtenerUsuario(mantenimiento.getTecnicoId());
            String destinatario = correoDestino(mantenimiento, mantenimiento.getCustodioId());
            if (destinatario == null || destinatario.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "El custodio no tiene correo registrado.");
                return "redirect:/mantenimiento/" + id;
            }

            List<Path> rutasImagenes = mantenimiento.getImagenes() == null ? List.of()
                    : mantenimiento.getImagenes().stream()
                            .map(img -> Paths.get(img.getRutaArchivo()))
                            .toList();
            byte[] pdfBytes = pdfService.generarInforme(mantenimiento, equipo, custodio, tecnico, rutasImagenes);
            archivoService.guardarPdf(id, pdfBytes);
            correoServicio.enviarInformeMantenimientoConPdf(
                    destinatario,
                    mantenimiento.getCustodioNombre(),
                    String.valueOf(mantenimiento.getIdMantenimiento()),
                    pdfBytes,
                    mantenimiento.getFechaMantenimiento(),
                    mantenimiento.getTipoMantenimiento(),
                    mantenimiento.getDetalle());
            redirectAttributes.addFlashAttribute("exito", "Correo reenviado a " + destinatario);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo reenviar el correo: " + e.getMessage());
        }
        return "redirect:/mantenimiento/" + id;
    }

    @GetMapping("/historial/{equipoId}")
    public String historial(@PathVariable Integer equipoId, Model model) {
        model.addAttribute("historial", mantenimientoManualServicio.obtenerHistorial(equipoId));
        model.addAttribute("equipo", equiposServicio.obtenerPorId(equipoId));
        return "mantenimiento/historial-mantenimiento";
    }

    @PostMapping("/cerrar/{id}")
    public String cerrar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        mantenimientoManualServicio.cerrar(id);
        redirectAttributes.addFlashAttribute("exito", "Orden cerrada correctamente");
        return "redirect:/mantenimiento/" + id;
    }

    @GetMapping("/programado")
    public String programado(Model model) {
        List<MantenimientoProgramadoResponseDTO> programados = mantenimientoProgramadoServicio.listarTodos();
        model.addAttribute("listaprogramados", programados);
        model.addAttribute("listaequipos", equiposServicio.listarEquipos());
        model.addAttribute("listausuarios", usuariosServicio.listarUsuario().stream().filter(UsuariosResponseDTO::isEstado).toList());
        model.addAttribute("vencidosProximos", mantenimientoProgramadoServicio.listarVencidosYProximos());
        return "mantenimiento/mantenimiento-programado";
    }

    @PostMapping("/programado/guardar")
    public String guardarProgramado(MantenimientoProgramadoRequestDTO request, RedirectAttributes redirectAttributes) {
        mantenimientoProgramadoServicio.guardar(request);
        redirectAttributes.addFlashAttribute("exito", "Programacion guardada correctamente");
        return "redirect:/mantenimiento/programado";
    }

    @PostMapping("/programado/desactivar")
    public String desactivarProgramado(@RequestParam Long idProgramado, RedirectAttributes redirectAttributes) {
        mantenimientoProgramadoServicio.desactivar(idProgramado);
        redirectAttributes.addFlashAttribute("exito", "Programacion desactivada correctamente");
        return "redirect:/mantenimiento/programado";
    }

    private Map<String, List<ActividadManualRequestDTO>> actividadesBase() {
        List<ActividadManualRequestDTO> base = actividadChecklistServicio.listarActivas().stream()
                .sorted(java.util.Comparator.comparing(ActividadChecklistResponseDTO::getCategoria)
                        .thenComparing(ActividadChecklistResponseDTO::getOrden, java.util.Comparator.nullsLast(Integer::compareTo)))
                .map(act -> {
                    ActividadManualRequestDTO dto = new ActividadManualRequestDTO();
                    dto.setIdActividad(act.getIdActividad());
                    dto.setNombreActividad(act.getNombre());
                    dto.setCategoriaActividad(act.getCategoria());
                    dto.setRealizada(Boolean.FALSE);
                    return dto;
                })
                .toList();
        base.forEach(a -> a.setRealizada(Boolean.FALSE));
        return base.stream().collect(Collectors.groupingBy(ActividadManualRequestDTO::getCategoriaActividad, LinkedHashMap::new, Collectors.toList()));
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

    private void completarDatosCustodio(MantenimientoManualResponseDTO mantenimiento, Integer custodioId) {
        if (custodioId == null) {
            return;
        }
        if (mantenimiento.getCustodioNombre() != null && !mantenimiento.getCustodioNombre().isBlank()
                && mantenimiento.getCustodioCorreo() != null && !mantenimiento.getCustodioCorreo().isBlank()) {
            return;
        }
        CustodiosResponseDTO custodio = custodiosServicio.obtenerPorId(custodioId);
        if (custodio == null) {
            return;
        }
        if (mantenimiento.getCustodioNombre() == null || mantenimiento.getCustodioNombre().isBlank()) {
            mantenimiento.setCustodioNombre(custodio.getNombre());
        }
        if (mantenimiento.getCustodioCorreo() == null || mantenimiento.getCustodioCorreo().isBlank()) {
            mantenimiento.setCustodioCorreo(custodio.getCorreo());
        }
    }

    private String correoDestino(MantenimientoManualResponseDTO mantenimiento, Integer custodioId) {
        completarDatosCustodio(mantenimiento, custodioId);
        return mantenimiento.getCustodioCorreo();
    }

    private String construirDetalleConObservaciones(String detalleBase, Map<String, String> requestParams) {
        StringBuilder builder = new StringBuilder(Objects.toString(detalleBase, "").trim());
        List<String> observaciones = requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("observacionCategoria["))
                .map(entry -> Map.entry(
                        entry.getKey().replace("observacionCategoria[", "").replace("]", ""),
                        Objects.toString(entry.getValue(), "").trim()))
                .filter(entry -> !entry.getValue().isBlank())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();

        if (!observaciones.isEmpty()) {
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("Observaciones por bloque:\n");
            observaciones.forEach(obs -> builder.append("- ").append(obs).append('\n'));
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
