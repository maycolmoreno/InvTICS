package com.uisrael.gestionactivosapi.presentacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.seguridad.JwtTokenProvider;
import com.uisrael.gestionactivosapi.infraestructura.seguridad.ServicioDetallesUsuario;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

	private final IUsuariosJpaRepositorio usuariosRepo;
	private final IRolModuloJpaRepositorio rolModuloRepo;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final ServicioDetallesUsuario servicioDetallesUsuario;

	public AuthControlador(IUsuariosJpaRepositorio usuariosRepo,
						   IRolModuloJpaRepositorio rolModuloRepo,
						   AuthenticationManager authenticationManager,
						   JwtTokenProvider jwtTokenProvider,
						   ServicioDetallesUsuario servicioDetallesUsuario) {
		this.usuariosRepo = usuariosRepo;
		this.rolModuloRepo = rolModuloRepo;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.servicioDetallesUsuario = servicioDetallesUsuario;
	}

	/**
	 * Valida credenciales y emite tokens JWT (access + refresh).
	 * Reemplaza el envio de Basic Auth en cada peticion desde el BFF.
	 */
	@PostMapping("/token")
	public ResponseEntity<Map<String, Object>> emitirToken(@RequestBody Map<String, String> body) {
		String correo = body.get("correo");
		String contrasena = body.get("contrasena");
		if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("error", "correo y contrasena son obligatorios"));
		}
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(correo, contrasena));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Credenciales invalidas"));
		}
		return ResponseEntity.ok(respuestaTokens(correo));
	}

	/**
	 * Emite un nuevo access token a partir de un refresh token valido,
	 * verificando que el usuario siga existiendo y activo.
	 */
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, Object>> refrescarToken(@RequestBody Map<String, String> body) {
		String refreshToken = body.get("refreshToken");
		if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Refresh token invalido o expirado"));
		}
		String correo = jwtTokenProvider.getUsernameFromToken(refreshToken);
		if (correo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Refresh token invalido"));
		}
		try {
			servicioDetallesUsuario.loadUserByUsername(correo);
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Usuario no disponible"));
		}
		return ResponseEntity.ok(respuestaTokens(correo));
	}

	private Map<String, Object> respuestaTokens(String correo) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("accessToken", jwtTokenProvider.generateAccessToken(correo));
		respuesta.put("refreshToken", jwtTokenProvider.generateRefreshToken(correo));
		respuesta.put("type", "Bearer");
		respuesta.put("expiresIn", jwtTokenProvider.getAccessTokenExpiration());
		return respuesta;
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
