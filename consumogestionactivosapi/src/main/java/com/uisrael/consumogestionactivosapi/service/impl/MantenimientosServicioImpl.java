package com.uisrael.consumogestionactivosapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoApiRequestDTO;
import com.uisrael.consumogestionactivosapi.service.IMantenimientosServicio;

@Service
public class MantenimientosServicioImpl implements IMantenimientosServicio {

	private final WebClient clienteWeb;

	public MantenimientosServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public void crearMantenimiento(MantenimientoApiRequestDTO dto) {
		try {
			clienteWeb.post().uri("/mantenimientos").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			System.out.println("STATUS: " + ex.getStatusCode());
			System.out.println("BODY: " + ex.getResponseBodyAsString());
			throw ex;
		}
	}
}
