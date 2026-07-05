package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
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
        return "Custodios/listarCustodios";
    }

    @GetMapping("/nuevo-custodio")
    public String nuevo(Model model) {
        CustodiosRequestDTO dto = new CustodiosRequestDTO();
        dto.setEstado(true);

        dto.setFkDepartamento(new DepartamentosRequestDTO());
        dto.getFkDepartamento().setIdDepartamento(0);

        dto.setFkCargo(new CargosRequestDTO());
        dto.getFkCargo().setIdCargo(0);

        dto.setFkUbicacion(new UbicacionesRequestDTO());
        dto.getFkUbicacion().setIdUbicacion(0);

        var departamentosActivos = servicioDepartamento.listarDepartamentos().stream().filter(u -> u.isEstado())
                .toList();

        var cargosActivos = servicioCargo.listarCargos().stream().filter(u -> u.isEstado()).toList();

        var ubicacionesActivas = servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList();

        model.addAttribute("listadepartamento", departamentosActivos);
        model.addAttribute("listacargo", cargosActivos);
        model.addAttribute("listaubicacion", ubicacionesActivas);

        model.addAttribute("custodio", dto);
        return "Custodios/nuevoCustodio";
    }

    @GetMapping("/editar-custodio/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        CustodiosResponseDTO dto = servicioCustodios.obtenerPorId(id);

        Integer idDepartamento = dto.getFkDepartamento() != null ? dto.getFkDepartamento().getIdDepartamento() : 0;

        model.addAttribute("listadepartamento",
                servicioDepartamento.listarDepartamentos().stream().filter(
                        departamento -> departamento.isEstado() || departamento.getIdDepartamento() == idDepartamento)
                        .toList());

        Integer idCargo = dto.getFkCargo() != null ? dto.getFkCargo().getIdCargo() : 0;

        model.addAttribute("listacargo", servicioCargo.listarCargos().stream()
                .filter(cargo -> cargo.isEstado() || cargo.getIdCargo() == idCargo).toList());

        model.addAttribute("listaubicacion", servicioUbicacion.listarUbicaciones().stream()
                .filter(u -> u.isEstado()
                        || (dto.getFkUbicacion() != null
                                && u.getIdUbicacion() == dto.getFkUbicacion().getIdUbicacion()))
                .toList());

        if (dto.getFkUbicacion() == null) {
            UbicacionesResponseDTO sinUbicacion = new UbicacionesResponseDTO();
            sinUbicacion.setIdUbicacion(0);
            dto.setFkUbicacion(sinUbicacion);
        }

        if (dto.getFkDepartamento() == null) {
            com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO sinDepartamento =
                    new com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO();
            sinDepartamento.setIdDepartamento(0);
            dto.setFkDepartamento(sinDepartamento);
        }

        if (dto.getFkCargo() == null) {
            com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO sinCargo =
                    new com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO();
            sinCargo.setIdCargo(0);
            dto.setFkCargo(sinCargo);
        }

        model.addAttribute("custodio", dto);

        return "Custodios/editarCustodio";
    }

    @PostMapping
    public String guardar(@ModelAttribute CustodiosRequestDTO custodio, Model model) {

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

        boolean hayErrores = false;

        if (custodio.getFechaIngreso() == null) {
            model.addAttribute("errorFechaInicio", "La fecha de ingreso es obligatoria");
            hayErrores = true;
        }

        if (custodio.getNombre() == null || custodio.getNombre().trim().isEmpty()) {
            model.addAttribute("errorNombre", "El nombre es obligatorio");
            hayErrores = true;
        }

        if (custodio.getCedula() == null || custodio.getCedula().trim().isEmpty()) {
            model.addAttribute("errorCedula", "La cédula es obligatoria");
            hayErrores = true;
        } else {
            boolean cedulaRepetida;

            if (custodio.getIdCustodio() > 0) {
                cedulaRepetida = servicioCustodios.existeCedulaParaOtro(custodio.getCedula().trim(),
                        custodio.getIdCustodio());
            } else {
                cedulaRepetida = servicioCustodios.existeCedula(custodio.getCedula().trim());
            }

            if (cedulaRepetida) {
                model.addAttribute("errorCedula", "Ya existe un empleado con esa cédula");
                hayErrores = true;
            }

            if (!CedulaEcuatorianaUtils.esValida(custodio.getCedula())) {
                model.addAttribute("errorCedula", "La cédula debe ser ecuatoriana válida de 10 dígitos");
                hayErrores = true;
            }
        }

        if (custodio.getTelefono() != null && !custodio.getTelefono().isBlank()) {
            if (!custodio.getTelefono().matches("\\d+")) {
                model.addAttribute("errorTelefono", "El teléfono solo debe contener números");
                hayErrores = true;
            }
        }

        if (custodio.getCorreo() == null || custodio.getCorreo().trim().isEmpty()) {
            model.addAttribute("errorCorreo", "El correo es obligatorio");
            hayErrores = true;
        } else {

            boolean correoRepetido;

            if (custodio.getIdCustodio() > 0) {
                correoRepetido = servicioCustodios.existeCorreoParaOtro(custodio.getCorreo().trim(),
                        custodio.getIdCustodio());
            } else {
                correoRepetido = servicioCustodios.existeCorreo(custodio.getCorreo().trim());
            }

            if (correoRepetido) {
                model.addAttribute("errorCorreo", "Ya existe un empleado con ese correo");
                hayErrores = true;
            }

            if (!custodio.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("errorCorreo", "Formato de correo inválido");
                hayErrores = true;
            }
        }

        if (custodio.getFkDepartamento().getIdDepartamento() > 0 && custodio.getFkCargo().getIdCargo() > 0) {
            var cargoSeleccionado = servicioCargo.obtenerPorId(custodio.getFkCargo().getIdCargo());
            boolean cargoInvalido = cargoSeleccionado == null || cargoSeleccionado.getFkDepartamento() == null
                    || cargoSeleccionado.getFkDepartamento().getIdDepartamento() != custodio.getFkDepartamento()
                            .getIdDepartamento();
            if (cargoInvalido) {
                model.addAttribute("errorSeleccionCargo", "El cargo no pertenece al departamento seleccionado");
                hayErrores = true;
            }
        }

        if (hayErrores) {

            model.addAttribute("listadepartamento",
                    servicioDepartamento.listarDepartamentos().stream().filter(u -> u.isEstado()).toList());

            model.addAttribute("listacargo", servicioCargo.listarCargos().stream().filter(u -> u.isEstado()).toList());

            model.addAttribute("listaubicacion",
                    servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList());

            model.addAttribute("custodio", custodio);

            return ubicacionesFormulario(custodio);
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
        } else {
            custodio.setEstado(true);
            servicioCustodios.crearCustodio(custodio);
        }

        return "redirect:/custodios";
    }

    @PostMapping("/toggle-custodio")
    public String toggle(@RequestParam Integer idCustodio, @RequestParam boolean estado) {

        servicioCustodios.actualizarEstado(idCustodio, estado);
        return "redirect:/custodios";
    }

    private String ubicacionesFormulario(CustodiosRequestDTO custodio) {
        return (custodio.getIdCustodio() > 0) ? "Custodios/editarCustodio" : "Custodios/nuevoCustodio";
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
