package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        actividades.sort(Comparator.comparing(ActividadChecklistResponseDTO::getOrden,
                Comparator.nullsLast(Comparator.naturalOrder())));

        model.addAttribute("actividades", actividades);
        return "checklist/listarChecklist";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/nueva")
    public String nueva() {
        return "redirect:/checklist";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/editar/{id}")
    public String editar() {
        return "redirect:/checklist";
    }

    @PostMapping
    public String guardar(@ModelAttribute ActividadChecklistRequestDTO actividad, RedirectAttributes redirectAttributes) {
        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            return error(redirectAttributes, "El nombre es obligatorio");
        }

        if (actividad.getIdActividad() > 0) {
            checklistServicio.actualizar(actividad.getIdActividad(), actividad);
            redirectAttributes.addFlashAttribute("success", "Actividad actualizada correctamente.");
        } else {
            actividad.setEstado(true);
            checklistServicio.crear(actividad);
            redirectAttributes.addFlashAttribute("success", "Actividad creada correctamente.");
        }
        return "redirect:/checklist";
    }

    private String error(RedirectAttributes redirectAttributes, String mensaje) {
        redirectAttributes.addFlashAttribute("error", mensaje);
        return "redirect:/checklist";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idActividad) {
        checklistServicio.eliminar(idActividad);
        return "redirect:/checklist";
    }
}
