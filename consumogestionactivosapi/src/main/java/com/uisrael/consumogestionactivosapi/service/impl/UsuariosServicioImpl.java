package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UsuariosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class UsuariosServicioImpl implements IUsuariosServicio {

	private final RestClient clienteweb;

	public UsuariosServicioImpl(RestClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<UsuariosResponseDTO> listarUsuario() {
		return clienteweb.get().uri("/usuarios").retrieve().body(new ParameterizedTypeReference<List<UsuariosResponseDTO>>() {});
	}

	@Override
	public void nuevoUsuario(UsuariosRequestDTO dto) {
		try {
			clienteweb.post().uri("/usuarios").body(dto).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public UsuariosResponseDTO obtenerUsuario(Integer id) {
		return clienteweb.get().uri("/usuarios/" + id).retrieve().body(UsuariosResponseDTO.class);
	}

	@Override
	public void actualizarUsuario(Integer id, UsuariosRequestDTO dto) {
		try {
			dto.setIdUsuario(id);
			clienteweb.put().uri("/usuarios").body(dto).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void eliminarUsuario(Integer id) {
		try {
			clienteweb.delete().uri("/usuarios/" + id).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

}
