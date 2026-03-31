package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

@Service
public class UbicacionesServicioImpl implements IUbicacionesServicio {

	private final RestClient clienteWeb;

	public UbicacionesServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<UbicacionesResponseDTO> listarUbicaciones() {

		return clienteWeb.get().uri("/ubicaciones").retrieve().body(new ParameterizedTypeReference<List<UbicacionesResponseDTO>>() {});
	}

	@Override
	public void crearUbicacion(UbicacionesRequestDTO dto) {
		clienteWeb.post().uri("/ubicaciones").body(dto).retrieve().toBodilessEntity();

	}

	@Override
	public UbicacionesResponseDTO obtenerPorId(Integer idUbicacion) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/ubicaciones/{id}").build(idUbicacion))
					.retrieve().body(UbicacionesResponseDTO.class);

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Ubicación no encontrada con id: " + idUbicacion);
			}
			throw e;
		}
	}

	@Override
	public void actualizarUbicacion(Integer idUbicacion, UbicacionesRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/ubicaciones/{id}").build(idUbicacion)).body(dto)
				.retrieve().toBodilessEntity();
	}

	@Override
	public void actualizarEstado(Integer idUbicacion, boolean estado) {
		UbicacionesRequestDTO dto = new UbicacionesRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/ubicaciones/estado/{id}", idUbicacion) // ✅ si tu baseUrl ya incluye /api
				.body(dto).retrieve().toBodilessEntity();

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
	                .body(Boolean.class);

	        return resp != null && resp;

	    } catch (RestClientResponseException e) {
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
	                .body(Boolean.class);

	        return resp != null && resp;

	    } catch (RestClientResponseException e) {
	        return false;
	    }
	}

}
