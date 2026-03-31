package com.uisrael.consumogestionactivosapi.service;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO;

@Service
public class CargosServicioImpl implements ICargosServicio {

	private final RestClient clienteWeb;

	public CargosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CargosResponseDTO> listarCargos() {
		return clienteWeb.get().uri("/cargos").retrieve().body(new ParameterizedTypeReference<List<CargosResponseDTO>>() {});
	}

	@Override
	public void crearCargo(CargosRequestDTO dto) {
		clienteWeb.post().uri("/cargos").body(dto).retrieve().toBodilessEntity();

	}

	@Override
	public CargosResponseDTO obtenerPorId(Integer idCargo) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/cargos/{id}").build(idCargo)).retrieve()
					.body(CargosResponseDTO.class);

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Cargo no encontrado con id: " + idCargo);
			}
			throw e;
		}
	}

	@Override
	public void actualizarCargo(Integer idCargo, CargosRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/cargos/{id}").build(idCargo)).body(dto).retrieve()
				.toBodilessEntity();

	}

	@Override
	public void actualizarEstado(Integer idCargo, boolean estado) {
		CargosRequestDTO dto = new CargosRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/cargos/estado/{id}", idCargo) // ✅ si tu baseUrl ya incluye /api
				.body(dto).retrieve().toBodilessEntity();

	}

	@Override
	public boolean nombreExiste(String nombre) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/cargos/existe-nombre").queryParam("nombre", nombre).build())
					.retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, int idCargo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/cargos/existe-nombre")
					.queryParam("nombre", nombre).queryParam("id", idCargo).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			return false;
		}
	}

}
