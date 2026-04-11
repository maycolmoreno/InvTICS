package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;

@Service
public class EquiposServicioImpl implements IEquiposServicio {

	private static final Logger logger = LoggerFactory.getLogger(EquiposServicioImpl.class);
	private final RestClient clienteWeb;

	public EquiposServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<EquiposResponseDTO> listarEquipos() {
		return clienteWeb.get().uri("/equipos").retrieve().body(new ParameterizedTypeReference<List<EquiposResponseDTO>>() {});
	}

	@Override
	public PaginaResponse<EquiposResponseDTO> listarEquiposPaginado(int page, int size) {
		return clienteWeb.get()
				.uri(uriBuilder -> uriBuilder.path("/equipos/paginado")
						.queryParam("page", page).queryParam("size", size).build())
				.retrieve()
				.body(new ParameterizedTypeReference<PaginaResponse<EquiposResponseDTO>>() {});
	}

	@Override
	public void crearEquipo(EquiposRequestDTO dto) {
		//clienteWeb.post().uri("/equipos").bodyValue(dto).retrieve().toBodilessEntity().block();

		try {
		    clienteWeb.post().uri("/equipos").body(dto).retrieve().toBodilessEntity();
		    logger.info("Equipo creado exitosamente");

		} catch (RestClientResponseException ex) {
		    logger.error("Error al crear equipo - STATUS: {}, BODY: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
		    throw ex;
		}
	}

	@Override
	public EquiposResponseDTO obtenerPorId(Integer idEquipo) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/{id}").build(idEquipo)).retrieve()
					.body(EquiposResponseDTO.class);
		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Equipo no encontrado con id: " + idEquipo);
			}
			throw e;
		}
	}

	@Override
	public void actualizarEquipo(Integer idEquipo, EquiposRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/equipos/{id}").build(idEquipo)).body(dto).retrieve()
				.toBodilessEntity();
	}

	@Override
	public void actualizarEstado(Integer idEquipo, boolean estado) {
		EquiposRequestDTO dto = new EquiposRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/equipos/estado/{id}", idEquipo).body(dto).retrieve().toBodilessEntity();
	}

	@Override
	public boolean existeCodigo(String codigo) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-codigo").queryParam("codigo", codigo).build())
					.retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public boolean existeCodigoParaOtro(String codigo, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-codigo")
					.queryParam("codigo", codigo).queryParam("id", idEquipo).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public boolean existeSerial(String serial) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-serial").queryParam("serial", serial).build())
					.retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public boolean existeSerialParaOtro(String serial, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-serial")
					.queryParam("serial", serial).queryParam("id", idEquipo).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public boolean existeMAC(String mac) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-mac").queryParam("mac", mac).build()).retrieve()
					.body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public boolean existeMACParaOtro(String mac, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-mac")
					.queryParam("mac", mac).queryParam("id", idEquipo).build()).retrieve().body(Boolean.class);

			return resp != null && resp;

		} catch (RestClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}
}
