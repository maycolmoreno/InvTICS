package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;

@Service
public class CustodiasServicioImpl implements ICustodiasServicio {

	private final RestClient clienteWeb;

	public CustodiasServicioImpl(RestClient clienteWeb) {
		this.clienteWeb = clienteWeb;
	}

	@Override
	public List<CustodiasResponseDTO> listarCustodias() {
		return clienteWeb.get().uri("/custodias").retrieve().body(new ParameterizedTypeReference<List<CustodiasResponseDTO>>() {});
	}

	@Override
	public void crearCustodia(CustodiasRequestDTO dto) {
		clienteWeb.post().uri("/custodias").body(dto).retrieve().toBodilessEntity();
	}

	// ✅ NUEVO: si tu API responde lista (como en tu Postman: [ { ... } ])
	@Override
	public List<CustodiasResponseDTO> crearCustodiaActa(CustodiasRequestDTO dto) {
		return clienteWeb.post().uri("/custodias").body(dto).retrieve().body(new ParameterizedTypeReference<List<CustodiasResponseDTO>>() {});
	}

	@Override
	public CustodiasResponseDTO obtenerPorId(Integer id) {
		return clienteWeb.get().uri(uriBuilder -> uriBuilder.path("/custodias/{id}").build(id)).retrieve()
				.body(CustodiasResponseDTO.class);
	}

	@Override
	public void actualizarCustodia(Integer id, CustodiasRequestDTO dto) {
		clienteWeb.put().uri(uriBuilder -> uriBuilder.path("/custodias/{id}").build(id)).body(dto).retrieve()
				.toBodilessEntity();
	}

	@Override
	public void actualizarEstado(Integer id, boolean estado) {
		CustodiasRequestDTO dto = new CustodiasRequestDTO();
		dto.setEstado(estado);

		clienteWeb.put().uri("/custodias/estado/{id}", id).body(dto).retrieve().toBodilessEntity();
	}
}
