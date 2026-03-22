package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;

@Service
public class CustodiasServicioImpl implements ICustodiasServicio {

	private final WebClient clienteWeb;

	public CustodiasServicioImpl(WebClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CustodiasResponseDTO> listarCustodias() {
		return clienteWeb.get().uri("/custodias").retrieve().bodyToFlux(CustodiasResponseDTO.class).collectList()
				.block();
	}

	@Override
	public void crearCustodia(CustodiasRequestDTO dto) {
		clienteWeb.post().uri("/custodias").bodyValue(dto).retrieve().toBodilessEntity().block();
	}

	// ✅ NUEVO: si tu API responde lista (como en tu Postman: [ { ... } ])
	@Override
	public List<CustodiasResponseDTO> crearCustodiaActa(CustodiasRequestDTO dto) {
		return clienteWeb.post().uri("/custodias").bodyValue(dto).retrieve().bodyToFlux(CustodiasResponseDTO.class)
				.collectList().block();
	}

	@Override
	public CustodiasResponseDTO obtenerPorId(Integer id) {
		return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodias/{id}").build(id)).retrieve()
				.bodyToMono(CustodiasResponseDTO.class).block();
	}

	@Override
	public void actualizarCustodia(Integer id, CustodiasRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/custodias/{id}").build(id)).bodyValue(dto).retrieve()
				.toBodilessEntity().block();
	}

	@Override
	public void actualizarEstado(Integer id, boolean estado) {
		CustodiasRequestDTO dto = new CustodiasRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/custodias/estado/{id}", id).bodyValue(dto).retrieve().toBodilessEntity().block();
	}
}
