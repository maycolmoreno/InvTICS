package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.repositorios.IDepartamentosRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.IRolesRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.IUsuariosRepositorio;

@RestController
@RequestMapping("/api/setup")
public class SetupControlador {

    private static final String DEPTO_DEFAULT = "Transformacion Digital";

    private final IUsuariosRepositorio usuariosRepositorio;
    private final IRolesRepositorio rolesRepositorio;
    private final IDepartamentosRepositorio departamentosRepositorio;
    private final PasswordEncoder passwordEncoder;

    public SetupControlador(IUsuariosRepositorio usuariosRepositorio,
                            IRolesRepositorio rolesRepositorio,
                            IDepartamentosRepositorio departamentosRepositorio,
                            PasswordEncoder passwordEncoder) {
        this.usuariosRepositorio = usuariosRepositorio;
        this.rolesRepositorio = rolesRepositorio;
        this.departamentosRepositorio = departamentosRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/necesario")
    public Map<String, Boolean> esNecesario() {
        boolean sinUsuarios = usuariosRepositorio.listarTodos().isEmpty();
        return Map.of("necesario", sinUsuarios);
    }

    @PostMapping("/admin")
    public ResponseEntity<Map<String, String>> crearAdmin(@RequestBody Map<String, String> datos) {
        if (datos == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El cuerpo de la solicitud es obligatorio."));
        }

        if (!usuariosRepositorio.listarTodos().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Ya existe al menos un usuario. Setup no permitido."));
        }

        String nombre = datos.get("nombre");
        String correo = datos.get("correo");
        String contrasena = datos.get("contrasena");
        String cedula = datos.get("cedula");

        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nombre, correo y contrasena son obligatorios."));
        }

        if (cedula == null || cedula.isBlank()) {
            cedula = "0000000000";
        }

        Roles rolAdmin = rolesRepositorio.buscarPorNombre("ADMINISTRADOR")
                .orElseGet(() -> rolesRepositorio.guardar(new Roles(0, "ADMINISTRADOR", true)));
        if (rolesRepositorio.buscarPorNombre("TECNICO").isEmpty()) {
            rolesRepositorio.guardar(new Roles(0, "TECNICO", true));
        }
        if (rolesRepositorio.buscarPorNombre("AUDITOR").isEmpty()) {
            rolesRepositorio.guardar(new Roles(0, "AUDITOR", true));
        }

        Departamentos depto;
        if (departamentosRepositorio.existeNombre(DEPTO_DEFAULT)) {
            depto = departamentosRepositorio.listarTodos().stream()
                    .filter(d -> DEPTO_DEFAULT.equals(d.getNombre()))
                    .findFirst().orElse(null);
        } else {
            depto = departamentosRepositorio.guardar(
                    new Departamentos(0, DEPTO_DEFAULT, true, null));
        }

        if (depto == null) {
            depto = departamentosRepositorio.guardar(
                    new Departamentos(0, DEPTO_DEFAULT, true, null));
        }

        String contrasenaEncriptada = passwordEncoder.encode(contrasena);
        Usuarios admin = new Usuarios(0, nombre, cedula, correo, contrasenaEncriptada, true, depto, rolAdmin);
        usuariosRepositorio.guardar(admin);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Administrador creado exitosamente."));
    }
}
