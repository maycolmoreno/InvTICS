package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

@Service
public class CategoriaEquiposServicioImpl implements ICategoriaEquiposServicio {

	private final WebClient clienteweb;

	public CategoriaEquiposServicioImpl(WebClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<CategoriaEquiposResponseDTO> listarCategoriaEquipo() {
		return clienteweb.get().uri("/categorias-equipo").retrieve().bodyToFlux(CategoriaEquiposResponseDTO.class).collectList().block();
	}

	@Override
	public void nuevoCategoriaEquipo(CategoriaEquiposRequestDTO dto) {
		try {
			clienteweb.post().uri("/categorias-equipo").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public CategoriaEquiposResponseDTO obtenerCategoriaEquipo(Integer id) {
		return clienteweb.get().uri("/categorias-equipo/" + id).retrieve().bodyToMono(CategoriaEquiposResponseDTO.class).block();
	}

	@Override
	public void actualizarCategoriaEquipo(Integer id, CategoriaEquiposRequestDTO dto) {
		try {
			dto.setIdCategoria(id);
			clienteweb.put().uri("/categorias-equipo").bodyValue(dto).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void eliminarCategoriaEquipo(Integer id) {
		try {
			clienteweb.delete().uri("/categorias-equipo/" + id).retrieve().toBodilessEntity().block();
		} catch (WebClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

}
