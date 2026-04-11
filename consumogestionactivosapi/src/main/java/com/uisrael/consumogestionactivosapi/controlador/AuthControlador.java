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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

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
			Map<String, Object> resp = RestClient.create(apiBaseUrl)
					.get()
					.uri("/setup/necesario")
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {
					});
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
			@RequestParam String cedula,
			@RequestParam String correo,
			@RequestParam String contrasena,
			@RequestParam String confirmar,
			Model model) {

		if (!setupNecesario()) {
			return "redirect:/login";
		}

		if (nombre == null || nombre.isBlank()) {
			model.addAttribute("error", "El nombre es obligatorio.");
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		if (correo == null || !correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
			model.addAttribute("error", "Ingrese un correo electronico valido.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		// Validar cédula obligatoria
		if (cedula == null || cedula.isBlank()) {
			model.addAttribute("error", "La cedula es obligatoria.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		if (!com.uisrael.consumogestionactivosapi.util.CedulaEcuatorianaUtils.esValida(cedula)) {
			model.addAttribute("error", "La cedula ingresada no es valida.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		if (contrasena == null || contrasena.length() < 8) {
			model.addAttribute("error", "La contrasena debe tener al menos 8 caracteres.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
		}

		// Validar complejidad de contraseña
		if (!contrasena.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
			model.addAttribute("error", "La contrasena debe incluir mayusculas, minusculas y numeros.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";
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
			datos.put("cedula", cedula);

			Map<String, Object> resp = RestClient.create(apiBaseUrl)
					.post()
					.uri("/setup/admin")
					.body(datos)
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {
					});

			if (resp != null && resp.containsKey("mensaje")) {
				return "redirect:/login?setupOk";
			}

			model.addAttribute("error", "No se pudo crear el administrador.");
			model.addAttribute("nombre", nombre);
			model.addAttribute("cedula", cedula);
			model.addAttribute("correo", correo);
			return "auth/setup";

		} catch (RestClientResponseException ex) {
			if (ex.getStatusCode() == org.springframework.http.HttpStatus.CONFLICT) {
				model.addAttribute("error", "Ya existe un administrador registrado con esos datos.");
			} else if (ex.getStatusCode() == org.springframework.http.HttpStatus.BAD_REQUEST) {
				model.addAttribute("error", "Datos invalidos. Verifique los campos e intente nuevamente.");
			} else {
				model.addAttribute("error", "Error al crear administrador. Intente nuevamente.");
			}
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

			RestClient clienteTemp = RestClient.builder()
					.baseUrl(apiBaseUrl)
					.defaultHeader("Authorization", "Basic " + credencialesBase64)
					.build();

			try {
				Map<String, Object> respuestaUsuario = clienteTemp.get()
						.uri("/auth/yo")
						.retrieve()
						.body(new ParameterizedTypeReference<Map<String, Object>>() {
						});

				if (respuestaUsuario != null) {
					String nombreUsuario = (String) respuestaUsuario.getOrDefault("nombreUsuario", correo.split("@")[0]);
					String rol = (String) respuestaUsuario.getOrDefault("rol", "AUDITOR");
					Integer idUsuario = respuestaUsuario.get("idUsuario") != null
							? ((Number) respuestaUsuario.get("idUsuario")).intValue()
							: null;
					sesionUsuario.iniciarSesion(correo, contrasena, nombreUsuario, rol, idUsuario);

					// Nombre completo y departamento del usuario (desde BD)
					String nombre = (String) respuestaUsuario.get("nombre");
					if (nombre != null && !nombre.isBlank()) {
						sesionUsuario.setNombre(nombre);
					} else {
						sesionUsuario.setNombre(nombreUsuario);
					}
					String departamento = (String) respuestaUsuario.get("departamento");
					if (departamento != null && !departamento.isBlank()) {
						sesionUsuario.setDepartamento(departamento);
					}

					// Cargar módulos permitidos del rol
					Object modulosObj = respuestaUsuario.get("modulos");
					if (modulosObj instanceof java.util.List<?> listaModulos) {
						java.util.Set<String> modulos = new java.util.HashSet<>();
						for (Object m : listaModulos) {
							if (m instanceof String s) {
								modulos.add(s);
							}
						}
						sesionUsuario.setModulosPermitidos(modulos);
					}

					return "redirect:/inicio";
				}

				sesionUsuario.iniciarSesion(correo, contrasena, correo.split("@")[0], "AUDITOR");
				return "redirect:/inicio";

			} catch (RestClientResponseException e) {
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
