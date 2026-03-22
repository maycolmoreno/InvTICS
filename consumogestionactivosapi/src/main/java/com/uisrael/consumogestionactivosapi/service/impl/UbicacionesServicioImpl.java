package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

@Service
public class UbicacionesServicioImpl implements IUbicacionesServicio {

	private final WebClient clienteWeb;

	public UbicacionesServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<UbicacionesResponseDTO> listarUbicaciones() {

		return clienteWeb.get().uri("/ubicaciones").retrieve().bodyToFlux(UbicacionesResponseDTO.class).collectList()
				.block();
	}

	@Override
	public void crearUbicacion(UbicacionesRequestDTO dto) {
		clienteWeb.post().uri("/ubicaciones").bodyValue(dto).retrieve().toBodilessEntity().block();

	}

	@Override
	public UbicacionesResponseDTO obtenerPorId(Integer idUbicacion) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/ubicaciones/{id}").build(idUbicacion))
					.retrieve().bodyToMono(UbicacionesResponseDTO.class).block();

		} catch (WebClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Ubicación no encontrada con id: " + idUbicacion);
			}
			throw e;
		}
	}

	@Override
	public void actualizarUbicacion(Integer idUbicacion, UbicacionesRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/ubicaciones/{id}").build(idUbicacion)).bodyValue(dto)
				.retrieve().toBodilessEntity().block();
	}

	@Override
	public void actualizarEstado(Integer idUbicacion, boolean estado) {
		UbicacionesRequestDTO dto = new UbicacionesRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/ubicaciones/estado/{id}", idUbicacion) // ✅ si tu baseUrl ya incluye /api
				.bodyValue(dto).retrieve().toBodilessEntity().block();

	}

	@Override
	public boolean nombreExiste(String nombre) {
	    try {
	        Boolean resp = clienteWeb.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/ubicaciones/existe-nombre")
	                        .queryParam("nombre", nombre)
	                        .build())
	                .retrieve()
	                .bodyToMono(Boolean.class)
	                .block();

	        return resp != null && resp;

	    } catch (WebClientResponseException e) {
	        // si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
	        return false;
	    }
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, int idUbicacion) {
	    try {
	        Boolean resp = clienteWeb.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/ubicaciones/existe-nombre")
	                        .queryParam("nombre", nombre)
	                        .queryParam("id", idUbicacion)
	                        .build())
	                .retrieve()
	                .bodyToMono(Boolean.class)
	                .block();

	        return resp != null && resp;

	    } catch (WebClientResponseException e) {
	        return false;
	    }
	}

}
