package com.uisrael.consumogestionactivosapi.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActaResumenDTO;
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

	@Override
	public ActasAgrupadas agruparPorActa() {
		List<CustodiasResponseDTO> lista = listarCustodias();

		Function<CustodiasResponseDTO, String> actaKeyFn = x -> {
			int idC = (x != null && x.getFkCustodio() != null) ? x.getFkCustodio().getIdCustodio()
					: (x != null ? x.getIdCustodio() : 0);
			String tipo = (x != null && x.getTipoMovimiento() != null) ? x.getTipoMovimiento() : "ASIGNACION";
			String fecha = (x != null && x.getFechaInicio() != null) ? x.getFechaInicio().toString() : "sin-fecha";
			return idC + "_" + tipo + "_" + fecha;
		};

		Map<String, List<CustodiasResponseDTO>> actaDetalles = lista.stream()
				.filter(x -> x != null)
				.collect(Collectors.groupingBy(actaKeyFn));

		List<ActaResumenDTO> actas = actaDetalles.entrySet().stream().map(entry -> {
			List<CustodiasResponseDTO> items = entry.getValue();
			CustodiasResponseDTO first = items.stream()
					.min(Comparator.comparing(CustodiasResponseDTO::getIdCustodiaEquipo, Comparator.nullsLast(Integer::compareTo)))
					.orElse(items.get(0));
			int idC = first.getFkCustodio() != null ? first.getFkCustodio().getIdCustodio() : first.getIdCustodio();
			String tipo = first.getTipoMovimiento() != null ? first.getTipoMovimiento() : "ASIGNACION";
			String etiqueta = switch (tipo) {
				case "ACTA_INICIAL" -> "Acta Inicial";
				case "TRASLADO" -> "Traslado";
				case "BAJA" -> "Baja";
				default -> "Asignacion";
			};
			boolean activa = items.stream().anyMatch(CustodiasResponseDTO::isEstado);
			LocalDate fechaFin = items.stream().map(CustodiasResponseDTO::getFechaFin)
					.filter(f -> f != null)
					.max(Comparator.naturalOrder()).orElse(null);
			int minPk = items.stream().map(CustodiasResponseDTO::getIdCustodiaEquipo)
					.filter(pk -> pk != null)
					.min(Integer::compareTo).orElse(0);

			ActaResumenDTO r = new ActaResumenDTO();
			r.setKey(entry.getKey());
			r.setIdCustodio(idC);
			r.setCustodio(first.getFkCustodio());
			r.setTipoMovimiento(tipo);
			r.setEtiquetaTipo(etiqueta);
			r.setFechaInicio(first.getFechaInicio());
			r.setFechaFin(fechaFin);
			r.setActiva(activa);
			r.setCantidadEquipos(items.size());
			r.setMinPk(minPk);
			r.setRutaActaPdf(first.getRutaActaPdf());
			r.setRutaActaFirmada(first.getRutaActaFirmada());
			return r;
		}).sorted(Comparator.comparingInt(ActaResumenDTO::getMinPk)).toList();

		// Numerar secuencialmente y luego ordenar para recientes primero
		List<ActaResumenDTO> actasNumeradas = new ArrayList<>(actas);
		for (int i = 0; i < actasNumeradas.size(); i++) {
			actasNumeradas.get(i).setNumeroActa(i + 1);
		}
		actasNumeradas.sort(Comparator.comparingInt(ActaResumenDTO::getMinPk).reversed());

		return new ActasAgrupadas(actasNumeradas, actaDetalles);
	}

	@Override
	public void subirActaFirmada(int idCustodia, MultipartFile archivo) {
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("archivo", new ByteArrayResource(archivo.getBytes()) {
				@Override
				public String getFilename() {
					return archivo.getOriginalFilename();
				}
			});
			clienteWeb.post()
					.uri("/custodias/{id}/acta-firmada", idCustodia)
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(body)
					.retrieve()
					.toBodilessEntity();
		} catch (java.io.IOException e) {
			throw new RuntimeException("Error al leer el archivo para subir", e);
		}
	}

	@Override
	public byte[] descargarActaFirmada(int idCustodia) {
		return clienteWeb.get()
				.uri("/custodias/{id}/acta-firmada", idCustodia)
				.retrieve()
				.body(byte[].class);
	}
}
