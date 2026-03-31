package com.uisrael.gestionactivosapi.presentacion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

	private final IUsuariosJpaRepositorio usuariosRepo;

	public AuthControlador(IUsuariosJpaRepositorio usuariosRepo) {
		this.usuariosRepo = usuariosRepo;
	}

	@GetMapping("/yo")
	public Map<String, Object> obtenerUsuarioActual(Authentication authentication) {
		Map<String, Object> respuesta = new HashMap<>();

		if (authentication != null && authentication.isAuthenticated()) {
			// Obtener el correo/username
			respuesta.put("correo", authentication.getName());

			// Obtener el primer rol (asumiendo que cada usuario tiene un solo rol)
			String rol = authentication.getAuthorities().stream()
				.findFirst()
				.map(GrantedAuthority::getAuthority)
				.orElse("ROLE_AUDITOR");

			// Remover el prefijo ROLE_ si existe
			if (rol.startsWith("ROLE_")) {
				rol = rol.substring(5);
			}

			respuesta.put("rol", rol);
			respuesta.put("nombreUsuario", authentication.getName().split("@")[0]);

			// Incluir ID de usuario para operaciones que lo requieran
			usuariosRepo.findByCorreo(authentication.getName())
				.ifPresent(usuario -> respuesta.put("idUsuario", usuario.getIdUsuario()));
		}

		return respuesta;
	}
}
