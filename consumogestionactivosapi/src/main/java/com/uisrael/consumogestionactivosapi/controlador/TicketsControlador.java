package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.TicketRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.TicketResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ITicketsServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketsControlador {

    private final ITicketsServicio ticketsServicio;
    private final ICustodiosServicio custodiosServicio;
    private final IEquiposServicio equiposServicio;
    private final IUsuariosServicio usuariosServicio;

    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idEquipo,
            @RequestParam(required = false) String odooTicketId,
            Model model) {
        List<TicketResponseDTO> tickets = ticketsServicio.listar(estado, idEquipo, odooTicketId);
        tickets.sort(Comparator.comparing(TicketResponseDTO::getCreadoEn,
                Comparator.nullsLast(Comparator.reverseOrder())));

        List<CustodiosResponseDTO> custodios = custodiosServicio.listarCustodios();
        List<EquiposResponseDTO> equipos = equiposServicio.listarEquipos();
        List<UsuariosResponseDTO> usuarios = usuariosServicio.listarUsuario();

        model.addAttribute("tickets", tickets);
        model.addAttribute("custodios", custodios);
        model.addAttribute("equipos", equipos);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroEquipo", idEquipo);
        model.addAttribute("filtroOdoo", odooTicketId);
        model.addAttribute("custodiosMap", indexarCustodios(custodios));
        model.addAttribute("equiposMap", indexarEquipos(equipos));
        model.addAttribute("usuariosMap", indexarUsuarios(usuarios));
        return "tickets/listarTickets";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        TicketRequestDTO ticket = new TicketRequestDTO();
        ticket.setEstado("ABIERTO");
        ticket.setPrioridad("MEDIA");
        ticket.setTipoOrigen("MANUAL");

        model.addAttribute("ticket", ticket);
        model.addAttribute("custodios", custodiosServicio.listarCustodios().stream().filter(CustodiosResponseDTO::isEstado).toList());
        model.addAttribute("equipos", equiposServicio.listarEquipos().stream().filter(EquiposResponseDTO::isEstado).toList());
        model.addAttribute("usuarios", usuariosServicio.listarUsuario().stream().filter(UsuariosResponseDTO::isEstado).toList());
        model.addAttribute("prioridades", List.of("BAJA", "MEDIA", "ALTA", "CRITICA"));
        model.addAttribute("estados", List.of("ABIERTO", "EN_REVISION", "ASIGNADO", "CERRADO", "CANCELADO"));
        model.addAttribute("origenes", List.of("MANUAL", "ODOO_HELPDESK"));
        return "tickets/nuevoTicket";
    }

    @PostMapping
    public String crear(@ModelAttribute("ticket") TicketRequestDTO ticket,
            RedirectAttributes redirectAttributes,
            Model model) {
        if ("ODOO_HELPDESK".equalsIgnoreCase(ticket.getTipoOrigen())
                && (ticket.getOdooTicketId() == null || ticket.getOdooTicketId().isBlank())) {
            model.addAttribute("errorGeneral", "El numero de ticket de Odoo es obligatorio cuando el origen es ODOO_HELPDESK.");
            recargarCombos(model);
            return "tickets/nuevoTicket";
        }

        try {
            TicketResponseDTO creado = ticketsServicio.crear(ticket);
            redirectAttributes.addFlashAttribute("exito",
                    "Ticket interno #" + creado.getIdTicket() + " creado correctamente.");
            return "redirect:/tickets";
        } catch (RuntimeException ex) {
            model.addAttribute("errorGeneral", ex.getMessage());
            recargarCombos(model);
            return "tickets/nuevoTicket";
        }
    }

    @PostMapping("/{idTicket}/asignar")
    public String asignar(@PathVariable Integer idTicket,
            @RequestParam Integer idTecnico,
            RedirectAttributes redirectAttributes) {
        try {
            TicketResponseDTO ticket = ticketsServicio.asignar(idTicket, idTecnico);
            redirectAttributes.addFlashAttribute("exito",
                    "Ticket #" + ticket.getIdTicket() + " asignado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorGeneral", ex.getMessage());
        }
        return "redirect:/tickets";
    }

    @PostMapping("/{idTicket}/cerrar")
    public String cerrar(@PathVariable Integer idTicket,
            RedirectAttributes redirectAttributes) {
        try {
            TicketResponseDTO ticket = ticketsServicio.cerrarYCrearMantenimiento(idTicket);
            redirectAttributes.addFlashAttribute("exito",
                    "Ticket #" + ticket.getIdTicket() + " cerrado y vinculado al mantenimiento #"
                            + ticket.getFkMantenimiento() + ".");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorGeneral", ex.getMessage());
        }
        return "redirect:/tickets";
    }

    private void recargarCombos(Model model) {
        model.addAttribute("custodios", custodiosServicio.listarCustodios().stream().filter(CustodiosResponseDTO::isEstado).toList());
        model.addAttribute("equipos", equiposServicio.listarEquipos().stream().filter(EquiposResponseDTO::isEstado).toList());
        model.addAttribute("usuarios", usuariosServicio.listarUsuario().stream().filter(UsuariosResponseDTO::isEstado).toList());
        model.addAttribute("prioridades", List.of("BAJA", "MEDIA", "ALTA", "CRITICA"));
        model.addAttribute("estados", List.of("ABIERTO", "EN_REVISION", "ASIGNADO", "CERRADO", "CANCELADO"));
        model.addAttribute("origenes", List.of("MANUAL", "ODOO_HELPDESK"));
    }

    private Map<Integer, String> indexarCustodios(List<CustodiosResponseDTO> custodios) {
        return custodios.stream().collect(Collectors.toMap(
                CustodiosResponseDTO::getIdCustodio,
                c -> c.getNombre() != null ? c.getNombre() : "Sin custodio",
                (a, b) -> a));
    }

    private Map<Integer, String> indexarEquipos(List<EquiposResponseDTO> equipos) {
        return equipos.stream().collect(Collectors.toMap(
                EquiposResponseDTO::getIdEquipo,
                e -> {
                    String nombre = e.getTipoEquipo() != null && !e.getTipoEquipo().isBlank()
                            ? e.getTipoEquipo()
                            : "Equipo";
                    String modelo = e.getModelo() != null && !e.getModelo().isBlank()
                            ? " " + e.getModelo()
                            : "";
                    String serial = e.getSerial() != null && !e.getSerial().isBlank()
                            ? " (" + e.getSerial() + ")"
                            : "";
                    return nombre + modelo + serial;
                },
                (a, b) -> a));
    }

    private Map<Integer, String> indexarUsuarios(List<UsuariosResponseDTO> usuarios) {
        return usuarios.stream().collect(Collectors.toMap(
                UsuariosResponseDTO::getIdUsuario,
                u -> u.getNombre() != null ? u.getNombre() : "Sin tecnico",
                (a, b) -> a));
    }
}
