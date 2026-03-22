package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;

@Service
public class CustodiosServicioImpl implements ICustodiosServicio {

	private final WebClient clienteWeb;

	public CustodiosServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CustodiosResponseDTO> listarCustodios() {
		return clienteWeb.get().uri("/custodios").retrieve().bodyToFlux(CustodiosResponseDTO.class).collectList()
				.block();
	}

	@Override
	public void crearCustodio(CustodiosRequestDTO dto) {
		clienteWeb.post().uri("/custodios").bodyValue(dto).retrieve().toBodilessEntity().block();
	}

	@Override
	public CustodiosResponseDTO obtenerPorId(Integer idCustodio) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/{id}").build(idCustodio)).retrieve()
					.bodyToMono(CustodiosResponseDTO.class).block();

		} catch (WebClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Custodio no encontrado con id: " + idCustodio);
			}
			throw e;
		}
	}

	@Override
	public void actualizarCustodio(Integer idCustodio, CustodiosRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/custodios/{id}").build(idCustodio)).bodyValue(dto)
				.retrieve().toBodilessEntity().block();
	}

	@Override
	public void actualizarEstado(Integer idCustodio, boolean estado) {

		CustodiosRequestDTO dto = new CustodiosRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/custodios/estado/{id}", idCustodio) // ✅ si tu baseUrl ya incluye /api
				.bodyValue(dto).retrieve().toBodilessEntity().block();
	}

	@Override
	public boolean existeCorreo(String correo) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/custodios/existe-correo").queryParam("correo", correo).build())
					.retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/existe-correo")
					.queryParam("correo", correo).queryParam("id", idCustodio).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}

	@Override
	public boolean existeCedula(String cedula) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/custodios/existe-cedula").queryParam("cedula", cedula).build())
					.retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/existe-cedula")
					.queryParam("cedula", cedula).queryParam("id", idCustodio).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}
}
