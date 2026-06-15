package com.uisrael.consumogestionactivosapi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.consumogestionactivosapi.exception.BackendException;

import org.springframework.web.client.RestClientResponseException;

public final class WebClientHelper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private WebClientHelper() {
	}

	public static String extraerMensajeError(RestClientResponseException ex) {
		try {
			String errorBody = ex.getResponseBodyAsString();
			if (errorBody == null || errorBody.isBlank()) {
				return "Error HTTP " + ex.getStatusCode().value() + ": " + ex.getStatusText();
			}

			JsonNode root = OBJECT_MAPPER.readTree(errorBody);
			if (root.hasNonNull("message")) {
				String message = root.get("message").asText();
				if (root.has("details") && root.get("details").isObject()) {
					for (JsonNode detalle : root.get("details")) {
						if (detalle != null && !detalle.asText().isBlank()) {
							return detalle.asText();
						}
					}
				}
				return message;
			}
			if (root.hasNonNull("error")) {
				return root.get("error").asText();
			}
			if (root.hasNonNull("detalle")) {
				return root.get("detalle").asText();
			}
			return errorBody;
		} catch (Exception e) {
			if (ex.getStatusText() != null && !ex.getStatusText().isBlank()) {
				return "Error HTTP " + ex.getStatusCode().value() + ": " + ex.getStatusText();
			}
			return "Error al procesar la solicitud";
		}
	}

	public static BackendException manejarError(RestClientResponseException ex) {
		return new BackendException(extraerMensajeError(ex));
	}
}
