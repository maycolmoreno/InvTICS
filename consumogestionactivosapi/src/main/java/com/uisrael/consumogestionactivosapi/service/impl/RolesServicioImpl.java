package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IRolesServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class RolesServicioImpl implements IRolesServicio {

	private final WebClient clienteweb;

	public RolesServicioImpl(WebClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<RolesResponseDTO> listarRol() {
		return clienteweb.get().uri("/roles").retrieve().bodyToFlux(RolesResponseDTO.class).collectList().block();
	}

	@Override
	public void nuevoRol(RolesRequestDTO dto) {
		try {
			clienteweb.post().uri("/roles").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public RolesResponseDTO obtenerRol(Integer id) {
		return clienteweb.get().uri("/roles/" + id).retrieve().bodyToMono(RolesResponseDTO.class).block();
	}

	@Override
	public void actualizarRol(Integer id, RolesRequestDTO dto) {
		try {
			dto.setIdRol(id);
			clienteweb.put().uri("/roles").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void eliminarRol(Integer id) {
		try {
			clienteweb.delete().uri("/roles/" + id).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

}
