package com.uisrael.consumogestionactivosapi.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoApiRequestDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientosServicio;

@Service
public class MantenimientosServicioImpl implements IMantenimientosServicio {

	private static final Logger logger = LoggerFactory.getLogger(MantenimientosServicioImpl.class);
	private final RestClient clienteWeb;

	public MantenimientosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public void crearMantenimiento(MantenimientoApiRequestDTO dto) {
		try {
			clienteWeb.post().uri("/mantenimientos").body(dto).retrieve().toBodilessEntity();
			logger.info("Mantenimiento creado exitosamente");
		} catch (RestClientResponseException ex) {
			logger.error("Error al crear mantenimiento - STATUS: {}, BODY: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
			throw ex;
		}
	}
}
