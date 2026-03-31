package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;

@Service
public class CustodiosServicioImpl implements ICustodiosServicio {

	private final RestClient clienteWeb;

	public CustodiosServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CustodiosResponseDTO> listarCustodios() {
		return clienteWeb.get().uri("/custodios").retrieve().body(new ParameterizedTypeReference<List<CustodiosResponseDTO>>() {});
	}

	@Override
	public void crearCustodio(CustodiosRequestDTO dto) {
		clienteWeb.post().uri("/custodios").body(dto).retrieve().toBodilessEntity();
	}

	@Override
	public CustodiosResponseDTO obtenerPorId(Integer idCustodio) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/{id}").build(idCustodio)).retrieve()
					.body(CustodiosResponseDTO.class);

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Custodio no encontrado con id: " + idCustodio);
			}
			throw e;
		}
	}

	@Override
	public void actualizarCustodio(Integer idCustodio, CustodiosRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/custodios/{id}").build(idCustodio)).body(dto)
				.retrieve().toBodilessEntity();
	}

	@Override
	public void actualizarEstado(Integer idCustodio, boolean estado) {

		CustodiosRequestDTO dto = new CustodiosRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/custodios/estado/{id}", idCustodio) // ✅ si tu baseUrl ya incluye /api
				.body(dto).retrieve().toBodilessEntity();
	}

	@Override
	public boolean existeCorreo(String correo) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/custodios/existe-correo").queryParam("correo", correo).build())
					.retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/existe-correo")
					.queryParam("correo", correo).queryParam("id", idCustodio).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			return false;
		}
	}

	@Override
	public boolean existeCedula(String cedula) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/custodios/existe-cedula").queryParam("cedula", cedula).build())
					.retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodios/existe-cedula")
					.queryParam("cedula", cedula).queryParam("id", idCustodio).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			return false;
		}
	}
}
