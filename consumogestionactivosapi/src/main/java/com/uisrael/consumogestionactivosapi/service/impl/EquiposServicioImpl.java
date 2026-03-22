package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;

@Service
public class EquiposServicioImpl implements IEquiposServicio {

	private final WebClient clienteWeb;

	public EquiposServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<EquiposResponseDTO> listarEquipos() {
		return clienteWeb.get().uri("/equipos").retrieve().bodyToFlux(EquiposResponseDTO.class).collectList().block();
	}

	@Override
	public void crearEquipo(EquiposRequestDTO dto) {
		//clienteWeb.post().uri("/equipos").bodyValue(dto).retrieve().toBodilessEntity().block();

		try {
		    clienteWeb.post().uri("/equipos").bodyValue(dto).retrieve().toBodilessEntity().block();


		} catch (WebClientResponseException ex) {
		    System.out.println("STATUS: " + ex.getStatusCode());
		    System.out.println("BODY: " + ex.getResponseBodyAsString());
		    throw ex; // o maneja el error devolviendo a la vista con mensaje
		}
	}

	@Override
	public EquiposResponseDTO obtenerPorId(Integer idEquipo) {
		try {
			return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/{id}").build(idEquipo)).retrieve()
					.bodyToMono(EquiposResponseDTO.class).block();
		} catch (WebClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new RuntimeException("Equipo no encontrado con id: " + idEquipo);
			}
			throw e;
		}
	}

	@Override
	public void actualizarEquipo(Integer idEquipo, EquiposRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/equipos/{id}").build(idEquipo)).bodyValue(dto).retrieve()
				.toBodilessEntity().block();
	}

	@Override
	public void actualizarEstado(Integer idEquipo, boolean estado) {
		EquiposRequestDTO dto = new EquiposRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/equipos/estado/{id}", idEquipo).bodyValue(dto).retrieve().toBodilessEntity().block();
	}

	@Override
	public boolean existeCodigo(String codigo) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-codigo").queryParam("codigo", codigo).build())
					.retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeCodigoParaOtro(String codigo, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-codigo")
					.queryParam("codigo", codigo).queryParam("id", idEquipo).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}

	@Override
	public boolean existeSerial(String serial) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-serial").queryParam("serial", serial).build())
					.retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeSerialParaOtro(String serial, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-serial")
					.queryParam("serial", serial).queryParam("id", idEquipo).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}

	@Override
	public boolean existeIP(String ip) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-ip").queryParam("ip", ip).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeIPParaOtro(String ip, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-ip").queryParam("ip", ip)
					.queryParam("id", idEquipo).build()).retrieve().bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}

	@Override
	public boolean existeMAC(String mac) {
		try {
			Boolean resp = clienteWeb.get()
					.uri(uriBuilder -> uriBuilder.path("/equipos/existe-mac").queryParam("mac", mac).build()).retrieve()
					.bodyToMono(Boolean.class).block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			// si tu API responde 404 o algo raro, por seguridad asumimos "no existe"
			return false;
		}
	}

	@Override
	public boolean existeMACParaOtro(String mac, int idEquipo) {
		try {
			Boolean resp = clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/equipos/existe-mac")
					.queryParam("mac", mac).queryParam("id", idEquipo).build()).retrieve().bodyToMono(Boolean.class)
					.block();

			return resp != null && resp;

		} catch (WebClientResponseException e) {
			return false;
		}
	}
}
