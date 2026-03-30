package com.uisrael.consumogestionactivosapi.controlador;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthControlador {

	private final SesionUsuario sesionUsuario;

	@Value("${api.base-url}")
	private String apiBaseUrl;

	public AuthControlador(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	private boolean setupNecesario() {
		try {
			Map<String, Object> resp = WebClient.create(apiBaseUrl)
					.get()
					.uri("/setup/necesario")
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
					})
					.block();
			return resp != null && Boolean.TRUE.equals(resp.get("necesario"));
		} catch (Exception e) {
			return false;
		}
	}

	@GetMapping("/")
	public String redirigirInicio() {
		if (setupNecesario()) {
			return "redirect:/setup";
		}
		if (sesionUsuario.isAutenticado()) {
			return "redirect:/inicio";
		}
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String mostrarLogin(Model model,
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String setupOk) {
		if (setupNecesario()) {
			return "redirect:/setup";
		}
		if (sesionUsuario.isAutenticado()) {
			return "redirect:/inicio";
		}
		if (error != null) {
			model.addAttribute("error", "Credenciales incorrectas. Por favor, intente nuevamente.");
		}
		if (setupOk != null) {
			model.addAttribute("exito", "Administrador creado exitosamente. Inicie sesion.");
		}
		return "auth/login";
	}

	@GetMapping("/setup")
	public String mostrarSetup(Model model) {
		if (!setupNecesario()) {
			return "redirect:/login";
		}
		return "auth/setup";
	}

	@PostMapping("/setup")
	public String procesarSetup(
			@RequestParam String nombre,
			@RequestParam(required = false) String cedula,
			@RequestParam String correo,
			@RequestParam String contrasena,
			@RequestParam String confirmar,
			Model model) {

		if (!setupNecesario()) {
			return "redirect:/login";
		}

		if (!contrasena.equals(confirmar)) {
			model.addAttribute("error", "Las contrasenas no coinciden.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		try {
			Map<String, String> datos = new java.util.HashMap<>();
			datos.put("nombre", nombre);
			datos.put("correo", correo);
			datos.put("contrasena", contrasena);
			if (cedula != null && !cedula.isBlank()) {
				datos.put("cedula", cedula);
			}

			Map<String, Object> resp = WebClient.create(apiBaseUrl)
					.post()
					.uri("/setup/admin")
					.bodyValue(datos)
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
					})
					.block();

			if (resp != null && resp.containsKey("mensaje")) {
				return "redirect:/login?setupOk";
			}

			model.addAttribute("error", "No se pudo crear el administrador.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";

		} catch (WebClientResponseException ex) {
			String body = ex.getResponseBodyAsString();
			model.addAttribute("error", body.contains("error") ? "Error: " + body : "Error al crear administrador.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		} catch (Exception e) {
			model.addAttribute("error", "Error de conexion con la API.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}
	}

	@PostMapping("/login")
	public String procesarLogin(
			@RequestParam String correo,
			@RequestParam String contrasena,
			Model model,
			HttpSession session) {

		try {
			String credenciales = correo + ":" + contrasena;
			String credencialesBase64 = Base64.getEncoder().encodeToString(credenciales.getBytes());

			WebClient clienteTemp = WebClient.builder()
					.baseUrl(apiBaseUrl)
					.defaultHeader("Authorization", "Basic " + credencialesBase64)
					.build();

			try {
				Map<String, Object> respuestaUsuario = clienteTemp.get()
						.uri("/auth/yo")
						.retrieve()
						.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
						})
						.block();

				if (respuestaUsuario != null) {
					String nombreUsuario = (String) respuestaUsuario.getOrDefault("nombreUsuario", correo.split("@")[0]);
					String rol = (String) respuestaUsuario.getOrDefault("rol", "AUDITOR");
					sesionUsuario.iniciarSesion(correo, contrasena, nombreUsuario, rol);
					return "redirect:/inicio";
				}

				sesionUsuario.iniciarSesion(correo, contrasena, correo.split("@")[0], "AUDITOR");
				return "redirect:/inicio";

			} catch (WebClientResponseException e) {
				if (e.getStatusCode().value() == 401) {
					return "redirect:/login?error";
				}
				model.addAttribute("error",
						"El API de autenticacion respondio con error " + e.getStatusCode().value() + ". Revise el backend.");
				model.addAttribute("correo", correo);
				return "auth/login";
			}

		} catch (Exception e) {
			model.addAttribute("error", "Error al procesar el login. Verifique que la API este funcionando.");
			model.addAttribute("correo", correo);
			return "auth/login";
		}
	}

	@GetMapping("/logout")
	public String cerrarSesion(HttpSession session) {
		sesionUsuario.cerrarSesion();
		session.invalidate();
		return "redirect:/login";
	}
}
