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
        return "usuarios/listarUsuarios";
    }

    @GetMapping("/nuevo-usuario")
    public String nuevoUsuario(Model model) {
        model.addAttribute("nuevousuario", new UsuariosRequestDTO());
        List<RolesResponseDTO> roles = servicioRoles.listarRol();
        model.addAttribute("roles", roles);
        List<DepartamentosResponseDTO> departamentos = servicioDepartamentos.listarDepartamentos();
        model.addAttribute("departamentos", departamentos);
        return "usuarios/nuevoUsuario";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute UsuariosRequestDTO nuevousuario, @RequestParam Integer fkRolId,
            @RequestParam Integer fkDepartamentoId, Model model) {
        if (!CedulaEcuatorianaUtils.esValida(nuevousuario.getCedula())) {
            model.addAttribute("errorGeneral", "La cédula debe ser ecuatoriana válida de 10 dígitos");
            model.addAttribute("nuevousuario", nuevousuario);
            List<RolesResponseDTO> roles = servicioRoles.listarRol();
            model.addAttribute("roles", roles);
            List<DepartamentosResponseDTO> departamentos = servicioDepartamentos.listarDepartamentos();
            model.addAttribute("departamentos", departamentos);
            return nuevousuario.getIdUsuario() > 0 ? "usuarios/editarUsuario" : "usuarios/nuevoUsuario";
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
            } else {
                servicioUsuarios.nuevoUsuario(nuevousuario);
            }
            return "redirect:/usuarios";
        } catch (BackendException e) {
            model.addAttribute("errorGeneral", e.getMessage());
            model.addAttribute("nuevousuario", nuevousuario);
            List<RolesResponseDTO> roles = servicioRoles.listarRol();
            model.addAttribute("roles", roles);
            List<DepartamentosResponseDTO> departamentos = servicioDepartamentos.listarDepartamentos();
            model.addAttribute("departamentos", departamentos);
            return nuevousuario.getIdUsuario() > 0 ? "usuarios/editarUsuario" : "usuarios/nuevoUsuario";
        }
    }

    @GetMapping("/editar-usuario/{id}")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        UsuariosResponseDTO usuarioResponse = servicioUsuarios.obtenerUsuario(id);

        UsuariosRequestDTO usuarioRequest = new UsuariosRequestDTO();
        usuarioRequest.setIdUsuario(usuarioResponse.getIdUsuario());
        usuarioRequest.setNombre(usuarioResponse.getNombre());
        usuarioRequest.setCedula(usuarioResponse.getCedula());
        usuarioRequest.setCorreo(usuarioResponse.getCorreo());
        usuarioRequest.setEstado(usuarioResponse.isEstado());

        if (usuarioResponse.getFkRol() != null) {
            RolesRequestDTO rol = new RolesRequestDTO();
            rol.setIdRol(usuarioResponse.getFkRol().getIdRol());
            rol.setNombre(usuarioResponse.getFkRol().getNombre());
            usuarioRequest.setFkRol(rol);
        }

        if (usuarioResponse.getFkDepartamento() != null) {
            DepartamentosRequestDTO departamento = new DepartamentosRequestDTO();
            departamento.setIdDepartamento(usuarioResponse.getFkDepartamento().getIdDepartamento());
            departamento.setNombre(usuarioResponse.getFkDepartamento().getNombre());
            usuarioRequest.setFkDepartamento(departamento);
        }

        List<RolesResponseDTO> roles = servicioRoles.listarRol();
        List<DepartamentosResponseDTO> departamentos = servicioDepartamentos.listarDepartamentos();
        model.addAttribute("nuevousuario", usuarioRequest);
        model.addAttribute("roles", roles);
        model.addAttribute("departamentos", departamentos);
        return "usuarios/editarUsuario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, Model model) {
        try {
            servicioUsuarios.eliminarUsuario(id);
            return "redirect:/usuarios";
        } catch (BackendException e) {
            List<UsuariosResponseDTO> contenidoBD = servicioUsuarios.listarUsuario();
            model.addAttribute("listarusuarios", contenidoBD);
            model.addAttribute("errorEliminar", e.getMessage());
            return "usuarios/listarUsuarios";
        }
    }
}
