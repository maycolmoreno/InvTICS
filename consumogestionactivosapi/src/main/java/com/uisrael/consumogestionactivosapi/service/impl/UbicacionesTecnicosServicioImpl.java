package com.uisrael.consumogestionactivosapi.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ConsentimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionTecnicoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialGpsResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionActivaResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesTecnicosServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class UbicacionesTecnicosServicioImpl implements IUbicacionesTecnicosServicio {

	private static final Logger logger = LoggerFactory.getLogger(UbicacionesTecnicosServicioImpl.class);
	private final RestClient clienteWeb;

	public UbicacionesTecnicosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public void registrarConsentimiento(ConsentimientoRequestDTO dto) {
		try {
			clienteWeb.post()
					.uri("/ubicaciones-tecnicos/consentimiento")
					.body(dto)
					.retrieve()
					.toBodilessEntity();
			logger.info("Consentimiento GPS registrado para tecnico {}", dto.getTecnicoId());
		} catch (RestClientResponseException ex) {
			logger.error("Error al registrar consentimiento GPS: {}", ex.getResponseBodyAsString());
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void enviarUbicacion(UbicacionTecnicoRequestDTO dto) {
		try {
			clienteWeb.post()
					.uri("/ubicaciones-tecnicos")
					.body(dto)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException ex) {
			logger.error("Error al enviar ubicacion GPS: {}", ex.getResponseBodyAsString());
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public List<UbicacionActivaResponseDTO> obtenerUbicacionesTiempoReal() {
		try {
			return clienteWeb.get()
					.uri("/ubicaciones-tecnicos/tiempo-real")
					.retrieve()
					.body(new ParameterizedTypeReference<List<UbicacionActivaResponseDTO>>() {});
		} catch (RestClientResponseException ex) {
			logger.error("Error al consultar ubicaciones tiempo real: {}", ex.getResponseBodyAsString());
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public List<HistorialGpsResponseDTO> obtenerHistorialGps(LocalDate fecha) {
		try {
			return clienteWeb.get()
					.uri("/ubicaciones-tecnicos/historial?fecha={fecha}", fecha.toString())
					.retrieve()
					.body(new ParameterizedTypeReference<List<HistorialGpsResponseDTO>>() {});
		} catch (RestClientResponseException ex) {
			logger.error("Error al consultar historial GPS: {}", ex.getResponseBodyAsString());
			throw WebClientHelper.manejarError(ex);
		}
	}
}
