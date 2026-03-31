package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IRolesServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class RolesServicioImpl implements IRolesServicio {

	private final RestClient clienteweb;

	public RolesServicioImpl(RestClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<RolesResponseDTO> listarRol() {
		return clienteweb.get().uri("/roles").retrieve().body(new ParameterizedTypeReference<List<RolesResponseDTO>>() {});
	}

	@Override
	public void nuevoRol(RolesRequestDTO dto) {
		try {
			clienteweb.post().uri("/roles").body(dto).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public RolesResponseDTO obtenerRol(Integer id) {
		return clienteweb.get().uri("/roles/" + id).retrieve().body(RolesResponseDTO.class);
	}

	@Override
	public void actualizarRol(Integer id, RolesRequestDTO dto) {
		try {
			dto.setIdRol(id);
			clienteweb.put().uri("/roles").body(dto).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void eliminarRol(Integer id) {
		try {
			clienteweb.delete().uri("/roles/" + id).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

}
