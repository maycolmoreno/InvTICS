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
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;

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
        List<ActividadManualRequestDTO> actividades = actividadesBase();
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
        byte[] pdfBytes = mantenimientoManualServicio.descargarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"mantenimiento_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
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
