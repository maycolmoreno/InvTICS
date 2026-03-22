package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UsuariosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class UsuariosServicioImpl implements IUsuariosServicio {

	private final WebClient clienteweb;

	public UsuariosServicioImpl(WebClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<UsuariosResponseDTO> listarUsuario() {
		return clienteweb.get().uri("/usuarios").retrieve().bodyToFlux(UsuariosResponseDTO.class).collectList().block();
	}

	@Override
	public void nuevoUsuario(UsuariosRequestDTO dto) {
		try {
			clienteweb.post().uri("/usuarios").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public UsuariosResponseDTO obtenerUsuario(Integer id) {
		return clienteweb.get().uri("/usuarios/" + id).retrieve().bodyToMono(UsuariosResponseDTO.class).block();
	}

	@Override
	public void actualizarUsuario(Integer id, UsuariosRequestDTO dto) {
		try {
			dto.setIdUsuario(id);
			clienteweb.put().uri("/usuarios").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void eliminarUsuario(Integer id) {
		try {
			clienteweb.delete().uri("/usuarios/" + id).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

}
