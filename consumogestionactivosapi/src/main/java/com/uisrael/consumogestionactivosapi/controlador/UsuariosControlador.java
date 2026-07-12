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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UsuariosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IRolesServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.exception.BackendException;
import com.uisrael.consumogestionactivosapi.util.CedulaEcuatorianaUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosControlador {

    private final IUsuariosServicio servicioUsuarios;
    private final IRolesServicio servicioRoles;
    private final IDepartamentosServicio servicioDepartamentos;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuariosResponseDTO> contenidoBD = servicioUsuarios.listarUsuario();
        contenidoBD.sort(Comparator.comparing(UsuariosResponseDTO::getIdUsuario));
        model.addAttribute("listarusuarios", contenidoBD);
        model.addAttribute("roles", servicioRoles.listarRol());
        model.addAttribute("departamentos", servicioDepartamentos.listarDepartamentos());
        return "usuarios/listarUsuarios";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/nuevo-usuario")
    public String nuevoUsuario() {
        return "redirect:/usuarios";
    }

    /** El alta/edicion ahora se hace desde un drawer en el listado. */
    @GetMapping("/editar-usuario/{id}")
    public String editarUsuario() {
        return "redirect:/usuarios";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuariosRequestDTO nuevousuario, @RequestParam Integer fkRolId,
            @RequestParam Integer fkDepartamentoId, RedirectAttributes redirectAttributes) {
        if (!CedulaEcuatorianaUtils.esValida(nuevousuario.getCedula())) {
            return error(redirectAttributes, "La cédula debe ser ecuatoriana válida de 10 dígitos");
        }

        try {
            RolesRequestDTO rol = new RolesRequestDTO();
            rol.setIdRol(fkRolId);
            nuevousuario.setFkRol(rol);

            DepartamentosRequestDTO departamento = new DepartamentosRequestDTO();
            departamento.setIdDepartamento(fkDepartamentoId);
            nuevousuario.setFkDepartamento(departamento);

            if (nuevousuario.getIdUsuario() > 0) {
                servicioUsuarios.actualizarUsuario(nuevousuario.getIdUsuario(), nuevousuario);
                redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
            } else {
                servicioUsuarios.nuevoUsuario(nuevousuario);
                redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
            }
        } catch (BackendException e) {
            return error(redirectAttributes, e.getMessage());
        }

        return "redirect:/usuarios";
    }

    private String error(RedirectAttributes redirectAttributes, String mensaje) {
        redirectAttributes.addFlashAttribute("error", mensaje);
        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            servicioUsuarios.eliminarUsuario(id);
        } catch (BackendException e) {
            redirectAttributes.addFlashAttribute("errorEliminar", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
