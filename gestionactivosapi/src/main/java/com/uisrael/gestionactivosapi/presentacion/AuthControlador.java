package com.uisrael.gestionactivosapi.presentacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

	private final IUsuariosJpaRepositorio usuariosRepo;
	private final IRolModuloJpaRepositorio rolModuloRepo;

	public AuthControlador(IUsuariosJpaRepositorio usuariosRepo,
						   IRolModuloJpaRepositorio rolModuloRepo) {
		this.usuariosRepo = usuariosRepo;
		this.rolModuloRepo = rolModuloRepo;
	}

	@GetMapping("/yo")
	public Map<String, Object> obtenerUsuarioActual(Authentication authentication) {
		Map<String, Object> respuesta = new HashMap<>();

		if (authentication != null && authentication.isAuthenticated()) {
			respuesta.put("correo", authentication.getName());

			String rol = authentication.getAuthorities().stream()
				.findFirst()
				.map(GrantedAuthority::getAuthority)
				.orElse("ROLE_AUDITOR");

			if (rol.startsWith("ROLE_")) {
				rol = rol.substring(5);
			}

			respuesta.put("rol", rol);
			respuesta.put("nombreUsuario", authentication.getName().split("@")[0]);

			usuariosRepo.findByCorreo(authentication.getName())
				.ifPresent(usuario -> {
					respuesta.put("idUsuario", usuario.getIdUsuario());
					respuesta.put("nombre", usuario.getNombre());
					if (usuario.getFkDepartamento() != null) {
						respuesta.put("departamento", usuario.getFkDepartamento().getNombre());
					}
				});

			List<String> modulos = rolModuloRepo.findCodigosModulosByRolNombre(rol);
			respuesta.put("modulos", modulos);
		}

		return respuesta;
	}
}
