package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICargosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.util.CedulaEcuatorianaUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/custodios")
public class CustodiosControlador {

    private final ICustodiosServicio servicioCustodios;
    private final IDepartamentosServicio servicioDepartamento;
    private final ICargosServicio servicioCargo;
    private final IUbicacionesServicio servicioUbicacion;

    @GetMapping
    public String listar(Model model) {
        List<CustodiosResponseDTO> lista = servicioCustodios.listarCustodios();
        lista.sort(Comparator.comparing(CustodiosResponseDTO::getIdCustodio));
        model.addAttribute("listarcustodios", lista);
        model.addAttribute("listadepartamento",
                servicioDepartamento.listarDepartamentos().stream().filter(d -> d.isEstado()).toList());
        model.addAttribute("listacargo", servicioCargo.listarCargos().stream().filter(c -> c.isEstado()).toList());
        model.addAttribute("listaubicacion",
                servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList());
        return "Custodios/listarCustodios";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/nuevo-custodio")
    public String nuevo() {
        return "redirect:/custodios";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/editar-custodio/{id}")
    public String editar() {
        return "redirect:/custodios";
    }

    /** Previsualiza en vivo los datos del directorio institucional para una cedula, sin guardar nada. */
    @GetMapping(value = "/directorio-preview", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> previsualizarDirectorio(@RequestParam String cedula) {
        return servicioCustodios.previsualizarDesdeDirectorio(cedula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("error", "La persona no aparece en el directorio institucional")));
    }

    @PostMapping
    public String guardar(@ModelAttribute CustodiosRequestDTO custodio, RedirectAttributes redirectAttributes) {

        // Asegura objeto anidado
        if (custodio.getFkDepartamento() == null) {
            custodio.setFkDepartamento(new DepartamentosRequestDTO());
        }
        if (custodio.getFkCargo() == null) {
            custodio.setFkCargo(new CargosRequestDTO());
        }
        if (custodio.getFkUbicacion() == null) {
            custodio.setFkUbicacion(new UbicacionesRequestDTO());
        }

        if (custodio.getFechaIngreso() == null) {
            return error(redirectAttributes, "La fecha de ingreso es obligatoria");
        }
        if (custodio.getNombre() == null || custodio.getNombre().trim().isEmpty()) {
            return error(redirectAttributes, "El nombre es obligatorio");
        }
        if (custodio.getCedula() == null || custodio.getCedula().trim().isEmpty()) {
            return error(redirectAttributes, "La cédula es obligatoria");
        }
        if (!CedulaEcuatorianaUtils.esValida(custodio.getCedula())) {
            return error(redirectAttributes, "La cédula debe ser ecuatoriana válida de 10 dígitos");
        }
        boolean cedulaRepetida = custodio.getIdCustodio() > 0
                ? servicioCustodios.existeCedulaParaOtro(custodio.getCedula().trim(), custodio.getIdCustodio())
                : servicioCustodios.existeCedula(custodio.getCedula().trim());
        if (cedulaRepetida) {
            return error(redirectAttributes, "Ya existe un empleado con esa cédula");
        }

        if (custodio.getTelefono() != null && !custodio.getTelefono().isBlank()
                && !custodio.getTelefono().matches("\\d+")) {
            return error(redirectAttributes, "El teléfono solo debe contener números");
        }

        if (custodio.getCorreo() == null || custodio.getCorreo().trim().isEmpty()) {
            return error(redirectAttributes, "El correo es obligatorio");
        }
        if (!custodio.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return error(redirectAttributes, "Formato de correo inválido");
        }
        boolean correoRepetido = custodio.getIdCustodio() > 0
                ? servicioCustodios.existeCorreoParaOtro(custodio.getCorreo().trim(), custodio.getIdCustodio())
                : servicioCustodios.existeCorreo(custodio.getCorreo().trim());
        if (correoRepetido) {
            return error(redirectAttributes, "Ya existe un empleado con ese correo");
        }

        if (custodio.getFkDepartamento().getIdDepartamento() > 0 && custodio.getFkCargo().getIdCargo() > 0) {
            var cargoSeleccionado = servicioCargo.obtenerPorId(custodio.getFkCargo().getIdCargo());
            boolean cargoInvalido = cargoSeleccionado == null || cargoSeleccionado.getFkDepartamento() == null
                    || cargoSeleccionado.getFkDepartamento().getIdDepartamento() != custodio.getFkDepartamento()
                            .getIdDepartamento();
            if (cargoInvalido) {
                return error(redirectAttributes, "El cargo no pertenece al departamento seleccionado");
            }
        }

        // Limpiar selects sin seleccionar (departamento/cargo del catalogo son opcionales)
        if (custodio.getFkUbicacion().getIdUbicacion() <= 0) {
            custodio.setFkUbicacion(null);
        }
        if (custodio.getFkDepartamento().getIdDepartamento() <= 0) {
            custodio.setFkDepartamento(null);
        }
        if (custodio.getFkCargo().getIdCargo() <= 0) {
            custodio.setFkCargo(null);
        }

        if (custodio.getIdCustodio() > 0) {
            servicioCustodios.actualizarCustodio(custodio.getIdCustodio(), custodio);
            redirectAttributes.addFlashAttribute("success", "Custodio actualizado correctamente.");
        } else {
            custodio.setEstado(true);
            servicioCustodios.crearCustodio(custodio);
            redirectAttributes.addFlashAttribute("success", "Custodio creado correctamente.");
        }

        return "redirect:/custodios";
    }

    private String error(RedirectAttributes redirectAttributes, String mensaje) {
        redirectAttributes.addFlashAttribute("error", mensaje);
        return "redirect:/custodios";
    }

    @PostMapping("/toggle-custodio")
    public String toggle(@RequestParam Integer idCustodio, @RequestParam boolean estado) {
        servicioCustodios.actualizarEstado(idCustodio, estado);
        return "redirect:/custodios";
    }

    @PostMapping("/eliminar-custodio")
    public String desactivar(@RequestParam Integer idCustodio) {
        servicioCustodios.actualizarEstado(idCustodio, false);
        return "redirect:/custodios";
    }

    @PostMapping("/activar-custodio")
    public String activar(@RequestParam Integer idCustodio) {
        servicioCustodios.actualizarEstado(idCustodio, true);
        return "redirect:/custodios";
    }
}
