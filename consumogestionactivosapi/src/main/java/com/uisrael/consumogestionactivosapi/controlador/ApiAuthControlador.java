package com.uisrael.consumogestionactivosapi.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.LoginRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.RefreshTokenRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.AuthResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.security.JwtTokenProvider;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthControlador {

	private static final Logger logger = LoggerFactory.getLogger(ApiAuthControlador.class);

	private final JwtTokenProvider jwtTokenProvider;

	@Value("${api.base-url}")
	private String apiBaseUrl;

	public ApiAuthControlador(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/**
	 * Endpoint para login - valida credenciales contra la API principal
	 * y retorna tokens JWT
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
		logger.info("Intento de login para usuario: {}", loginRequest.getCorreo());
		
		try {
			// Validar credenciales contra la API principal
			String credenciales = loginRequest.getCorreo() + ":" + loginRequest.getContrasena();
			String credencialesBase64 = Base64.getEncoder().encodeToString(credenciales.getBytes());

			WebClient clienteTemp = WebClient.builder()
					.baseUrl(apiBaseUrl)
					.defaultHeader("Authorization", "Basic " + credencialesBase64)
					.build();

			// La API principal expone el usuario autenticado en /api/auth/yo usando Basic Auth
			Map<String, String> usuarioActual = clienteTemp
					.get()
					.uri("/auth/yo")
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
					})
					.block();

			if (usuarioActual == null || usuarioActual.isEmpty()) {
				logger.warn("Usuario no encontrado: {}", loginRequest.getCorreo());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Credenciales inválidas"));
			}

			UsuariosResponseDTO usuario = new UsuariosResponseDTO();
			usuario.setCorreo(usuarioActual.getOrDefault("correo", loginRequest.getCorreo()));
			usuario.setNombre(usuarioActual.getOrDefault("nombreUsuario", loginRequest.getCorreo()));
			usuario.setEstado(true);

			RolesResponseDTO rol = new RolesResponseDTO();
			rol.setNombre(usuarioActual.getOrDefault("rol", "AUDITOR"));
			rol.setEstado(true);
			usuario.setFkRol(rol);

			// Generar tokens JWT
			String accessToken = jwtTokenProvider.generateAccessToken(usuario.getCorreo());
			String refreshToken = jwtTokenProvider.generateRefreshToken(usuario.getCorreo());

			AuthResponseDTO response = new AuthResponseDTO();
			response.setAccessToken(accessToken);
			response.setRefreshToken(refreshToken);
			response.setType("Bearer");
			response.setExpiresIn(jwtTokenProvider.getAccessTokenExpiration());
			response.setUsuario(usuario);

			logger.info("Autenticación exitosa para usuario: {}", usuario.getCorreo());
			
			return ResponseEntity.ok(response);

		} catch (WebClientResponseException ex) {
			logger.warn("Error de autenticación - credenciales inválidas");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Correo o contraseña incorrectos"));
		} catch (Exception ex) {
			logger.error("Error en login: {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error en el servidor"));
		}
	}

	/**
	 * Endpoint para refrescar el access token usando el refresh token
	 */
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO refreshRequest) {
		try {
			String refreshToken = refreshRequest.getRefreshToken();

			// Validar el refresh token
			if (!jwtTokenProvider.validateToken(refreshToken)) {
				logger.warn("Refresh token inválido o expirado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Refresh token inválido o expirado"));
			}

			// Extraer el username del refresh token
			String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

			if (username == null) {
				logger.warn("No se pudo extraer el username del refresh token");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Token inválido"));
			}

			// Generar nuevo access token
			String newAccessToken = jwtTokenProvider.generateAccessToken(username);

			Map<String, Object> response = new HashMap<>();
			response.put("accessToken", newAccessToken);
			response.put("refreshToken", refreshToken);
			response.put("type", "Bearer");
			response.put("expiresIn", jwtTokenProvider.getAccessTokenExpiration());

			logger.info("Token refrescado exitosamente para usuario: {}", username);
			
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			logger.error("Error al refrescar token: {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error al refrescar token"));
		}
	}
}
