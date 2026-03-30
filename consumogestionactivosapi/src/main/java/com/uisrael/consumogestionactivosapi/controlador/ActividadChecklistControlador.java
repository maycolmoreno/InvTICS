package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ActividadChecklistRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/checklist")
@RequiredArgsConstructor
public class ActividadChecklistControlador {

    private final IActividadChecklistServicio checklistServicio;

    @GetMapping
    public String listar(Model model) {
        List<ActividadChecklistResponseDTO> actividades = checklistServicio.listarActivas();
        actividades.sort(Comparator
                .comparing(ActividadChecklistResponseDTO::getCategoria, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ActividadChecklistResponseDTO::getOrden, Comparator.nullsLast(Comparator.naturalOrder())));

        Map<String, List<ActividadChecklistResponseDTO>> porCategoria = actividades.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategoria() != null ? a.getCategoria() : "Sin categoria",
                        Collectors.toList()));

        model.addAttribute("actividades", actividades);
        model.addAttribute("porCategoria", porCategoria);
        model.addAttribute("totalActividades", actividades.size());
        model.addAttribute("totalCategorias", porCategoria.size());
        return "checklist/listarChecklist";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        ActividadChecklistRequestDTO actividad = new ActividadChecklistRequestDTO();
        actividad.setEstado(true);
        actividad.setOrden(1);
        model.addAttribute("actividad", actividad);
        return "checklist/nuevaActividad";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        ActividadChecklistResponseDTO actividad = checklistServicio.obtenerPorId(id);
        model.addAttribute("actividad", actividad);
        return "checklist/editarActividad";
    }

    @PostMapping
    public String guardar(@ModelAttribute ActividadChecklistRequestDTO actividad, Model model) {
        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            model.addAttribute("errorNombre", "El nombre es obligatorio");
            model.addAttribute("actividad", actividad);
            return formulario(actividad);
        }
        if (actividad.getCategoria() == null || actividad.getCategoria().trim().isEmpty()) {
            model.addAttribute("errorCategoria", "La categoria es obligatoria");
            model.addAttribute("actividad", actividad);
            return formulario(actividad);
        }

        if (actividad.getIdActividad() > 0) {
            checklistServicio.actualizar(actividad.getIdActividad(), actividad);
        } else {
            actividad.setEstado(true);
            checklistServicio.crear(actividad);
        }
        return "redirect:/checklist";
    }

    @PostMapping("/eliminar")
    public String eliminar(@org.springframework.web.bind.annotation.RequestParam Integer idActividad) {
        checklistServicio.eliminar(idActividad);
        return "redirect:/checklist";
    }

    private String formulario(ActividadChecklistRequestDTO actividad) {
        return (actividad.getIdActividad() > 0) ? "checklist/editarActividad" : "checklist/nuevaActividad";
    }
}
