package com.uisrael.consumogestionactivosapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ErrorResponseDTO;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RestClientResponseException.class)
	public Object handleRestClientResponseException(RestClientResponseException ex,
			HttpServletRequest request) {

		int status = ex.getStatusCode().value();
		String message = resolverMensaje(ex, status);

		log.warn("Backend API error [{}] en {}: {}", status, request.getRequestURI(), message);

		if (esPeticionApi(request)) {
			return respuestaJson(status, message, request);
		}

		if (status == 401) {
			return new ModelAndView("redirect:/login?expired");
		}

		return vistaError(status, message);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public Object handleResourceAccessException(ResourceAccessException ex,
			HttpServletRequest request) {

		log.error("Error de conexión con backend en {}: {}", request.getRequestURI(), ex.getMessage());
		String message = "No se pudo conectar con el servicio backend. Intente nuevamente.";

		if (esPeticionApi(request)) {
			return respuestaJson(503, message, request);
		}
		return vistaError(503, message);
	}

	@ExceptionHandler(Exception.class)
	public Object handleException(Exception ex, HttpServletRequest request) {

		log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
		String message = "Ocurrió un error inesperado. Por favor contacte al administrador.";

		if (esPeticionApi(request)) {
			return respuestaJson(500, message, request);
		}
		return vistaError(500, message);
	}

	// -------------------------------------------------------------------------
	// Métodos privados de apoyo
	// -------------------------------------------------------------------------

	private boolean esPeticionApi(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String accept = request.getHeader("Accept");
		String requestedWith = request.getHeader("X-Requested-With");

		return uri.startsWith("/api/")
				|| "XMLHttpRequest".equals(requestedWith)
				|| (accept != null && accept.contains("application/json") && !accept.contains("text/html"));
	}

	private String resolverMensaje(RestClientResponseException ex, int status) {
		return switch (status) {
			case 400 -> {
				String backendMsg = WebClientHelper.extraerMensajeError(ex);
				yield (backendMsg != null && !backendMsg.isBlank())
						? backendMsg
						: "Datos inválidos. Verifique la información enviada.";
			}
			case 401 -> "Su sesión ha expirado. Por favor inicie sesión nuevamente.";
			case 403 -> "No tiene permisos para realizar esta acción.";
			case 404 -> "El recurso solicitado no fue encontrado.";
			case 500 -> "El servicio backend no pudo procesar la solicitud.";
			default -> {
				String backendMsg = WebClientHelper.extraerMensajeError(ex);
				yield (backendMsg != null && !backendMsg.isBlank())
						? backendMsg
						: "Error al comunicarse con el servicio backend (HTTP " + status + ").";
			}
		};
	}

	private ResponseEntity<ErrorResponseDTO> respuestaJson(int status, String message, HttpServletRequest request) {
		ErrorResponseDTO dto = new ErrorResponseDTO(true, status, message, request.getRequestURI());
		return ResponseEntity.status(status).body(dto);
	}

	private ModelAndView vistaError(int status, String message) {
		ModelAndView mav = new ModelAndView("error/backend-error");
		mav.addObject("errorStatus", status);
		mav.addObject("errorMessage", message);
		return mav;
	}
}
