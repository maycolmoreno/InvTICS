package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ModuloResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IModulosServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class ModulosServicioImpl implements IModulosServicio {

	private static final Logger logger = LoggerFactory.getLogger(ModulosServicioImpl.class);
	private final RestClient clienteWeb;

	public ModulosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<ModuloResponseDTO> listarModulos() {
		return clienteWeb.get()
				.uri("/modulos")
				.retrieve()
				.body(new ParameterizedTypeReference<List<ModuloResponseDTO>>() {});
	}

	@Override
	public List<ModuloResponseDTO> listarModulosPorRol(Integer rolId) {
		return clienteWeb.get()
				.uri("/modulos/por-rol/{rolId}", rolId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<ModuloResponseDTO>>() {});
	}

	@Override
	public void actualizarModulosRol(Integer rolId, List<Integer> moduloIds) {
		try {
			clienteWeb.put()
					.uri("/modulos/por-rol/{rolId}", rolId)
					.body(moduloIds)
					.retrieve()
					.toBodilessEntity();
			logger.info("Permisos actualizados para rol {}", rolId);
		} catch (RestClientResponseException ex) {
			logger.error("Error al actualizar permisos: {}", ex.getResponseBodyAsString());
			throw WebClientHelper.manejarError(ex);
		}
	}
}
