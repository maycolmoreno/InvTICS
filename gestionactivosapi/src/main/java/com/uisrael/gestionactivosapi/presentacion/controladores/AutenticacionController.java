package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IAutenticarUsuarioUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;

@RestController
@RequestMapping("/api/autenticacion")
@CrossOrigin(origins = "*")
public class AutenticacionController {

	private final IAutenticarUsuarioUseCase autenticarUsuarioUseCase;

	public AutenticacionController(IAutenticarUsuarioUseCase autenticarUsuarioUseCase) {
		this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
	}

	@PostMapping("/login")
	public ResponseEntity<Usuarios> login(@RequestParam String correo, @RequestParam String contrasena) {
		Optional<Usuarios> usuario = autenticarUsuarioUseCase.ejecutar(correo, contrasena);
		return usuario
				.map(this::sinContrasena)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(401).build());
	}

	private Usuarios sinContrasena(Usuarios usuario) {
		return new Usuarios(
				usuario.getIdUsuario(),
				usuario.getNombre(),
				usuario.getCedula(),
				usuario.getCorreo(),
				null,
				usuario.isEstado(),
				usuario.getFkDepartamento(),
				usuario.getFkRol());
	}
}
