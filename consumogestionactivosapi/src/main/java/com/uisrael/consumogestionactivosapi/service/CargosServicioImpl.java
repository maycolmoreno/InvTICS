package com.uisrael.consumogestionactivosapi.service;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO;

@Service
public class CargosServicioImpl implements ICargosServicio {

	private final WebClient clienteWeb;

	public CargosServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CargosResponseDTO> listarCargos() {
		return clienteWeb.get().uri("/cargos").retrieve().bodyToFlux(CargosResponseDTO.class).collectList().block();
	}

	@Override
	public void crearCargo(CargosRequestDTO dto) {
		clienteWeb.post().uri("/cargos").bodyValue(dto).retrieve().toBodilessEntity().block();

	}

	@Override
	public CargosResponseDTO obtenerPorId(Integer idCargo) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/cargos/{id}").build(idCargo)).retrieve()
					.bodyToMono(CargosResponseDTO.class).block();

		} catch (WebClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Cargo no encontrado con id: " + idCargo);
			}
			throw e;
		}
	}

	@Override
	public void actualizarCargo(Integer idCargo, CargosRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/cargos/{id}").build(idCargo)).bodyValue(dto).retrieve()
				.toBodilessEntity().block();

	}

	@Override
	public void actualizarEstado(Integer idCargo, boolean estado) {
		CargosRequestDTO dto = new CargosRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/cargos/estado/{id}", idCargo) // ✅ si tu baseUrl ya incluye /api
				.bodyValue(dto).retrieve().toBodilessEntity().block();

	}

	@Override
	public boolean nombreExiste(String nombre) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/cargos/existe-nombre").queryParam("nombre", nombre).build())
					.retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean nombreExisteParaOtro(String nombre, int idCargo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/cargos/existe-nombre")
					.queryParam("nombre", nombre).queryParam("id", idCargo).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}

}
