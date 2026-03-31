package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;

@Service
public class DepartamentosServicioImpl implements IDepartamentosServicio {

	private final RestClient clienteWeb;

	public DepartamentosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<DepartamentosResponseDTO> listarDepartamentos() {

		return clienteWeb.get().uri("/departamentos").retrieve().body(new ParameterizedTypeReference<List<DepartamentosResponseDTO>>() {});
	}

	@Override
	public void crearDepartamento(DepartamentosRequestDTO dto) {

		clienteWeb.post().uri("/departamentos").body(dto).retrieve().toBodilessEntity();
	}

	@Override
	public DepartamentosResponseDTO obtenerPorId(Integer idDepartamento) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/departamentos/{id}").build(idDepartamento))
					.retrieve().body(DepartamentosResponseDTO.class);

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Departamento no encontrado con id: " + idDepartamento);
			}
			throw e;
		}
	}

	@Override
	public void actualizarDepartamento(Integer idDepartamento, DepartamentosRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/departamentos/{id}").build(idDepartamento)).body(dto)
				.retrieve().toBodilessEntity();

	}

	@Override
	public void actualizarEstado(Integer idDepartamento, boolean estado) {
		DepartamentosRequestDTO dto = new DepartamentosRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/departamentos/estado/{id}", idDepartamento) // ✅ si tu baseUrl ya incluye /api
				.body(dto).retrieve().toBodilessEntity();

	}

	@Override
	public boolean nombreExiste(String nombre) {
	    try {
	        Boolean resp = clienteWeb.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/departamentos/existe-nombre")
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
	public boolean nombreExisteParaOtro(String nombre, int idDepartamento) {
	    try {
	        Boolean resp = clienteWeb.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/departamentos/existe-nombre")
	                        .queryParam("nombre", nombre)
	                        .queryParam("id", idDepartamento)
	                        .build())
	                .retrieve()
	                .body(Boolean.class);

	        return resp != null && resp;

	    } catch (RestClientResponseException e) {
	        return false;
	    }
	}

}
