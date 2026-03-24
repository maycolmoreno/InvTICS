package com.uisrael.consumogestionactivosapi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoApiRequestDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientosServicio;

@Service
public class MantenimientosServicioImpl implements IMantenimientosServicio {

	private static final Logger logger = LoggerFactory.getLogger(MantenimientosServicioImpl.class);
	private final WebClient clienteWeb;

	public MantenimientosServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public void crearMantenimiento(MantenimientoApiRequestDTO dto) {
		try {
			clienteWeb.post().uri("/mantenimientos").bodyValue(dto).retrieve().toBodilessEntity().block();
			logger.info("Mantenimiento creado exitosamente");
		} catch (WebClientResponseException ex) {
			logger.error("Error al crear mantenimiento - STATUS: {}, BODY: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
			throw ex;
		}
	}
}
