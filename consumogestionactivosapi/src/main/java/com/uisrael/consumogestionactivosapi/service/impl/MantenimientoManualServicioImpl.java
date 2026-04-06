package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.util.WebClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoManualServicioImpl implements IMantenimientoManualServicio {

	private final RestClient clienteWeb;

	@Override
	public MantenimientoManualResponseDTO crear(MantenimientoManualRequestDTO request) {
		try {
			return clienteWeb.post().uri("/mantenimiento").body(request).retrieve()
					.body(MantenimientoManualResponseDTO.class);
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoRequestDTO> imagenes) {
		try {
			clienteWeb.post().uri("/mantenimiento/{id}/imagenes", idMantenimiento).body(imagenes).retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public List<ImagenMantenimientoRequestDTO> subirImagenes(Integer idMantenimiento, List<MultipartFile> imagenes) {
		if (imagenes == null || imagenes.isEmpty()) {
			return List.of();
		}
		try {
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			for (MultipartFile imagen : imagenes) {
				if (imagen == null || imagen.isEmpty()) {
					continue;
				}
				Resource resource = imagen.getResource();
				String filename = imagen.getOriginalFilename() != null ? imagen.getOriginalFilename() : "evidencia";
				MediaType contentType = MediaType.parseMediaType(
						imagen.getContentType() != null ? imagen.getContentType()
								: MediaType.APPLICATION_OCTET_STREAM_VALUE);
				HttpHeaders partHeaders = new HttpHeaders();
				partHeaders.setContentType(contentType);
				partHeaders.setContentDispositionFormData("files", filename);
				parts.add("files", new HttpEntity<>(resource, partHeaders));
			}
			return clienteWeb.post().uri("/mantenimiento/{id}/imagenes/upload", idMantenimiento)
					.contentType(MediaType.MULTIPART_FORM_DATA).body(parts).retrieve()
					.body(new ParameterizedTypeReference<List<ImagenMantenimientoRequestDTO>>() {
					});
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public List<MantenimientoManualResponseDTO> listarTodos() {
		return clienteWeb.get().uri("/mantenimiento").retrieve()
				.body(new ParameterizedTypeReference<List<MantenimientoManualResponseDTO>>() {
				});
	}

	@Override
	public PaginaResponse<MantenimientoManualResponseDTO> listarTodosPaginado(int page, int size) {
		return clienteWeb.get()
				.uri(uriBuilder -> uriBuilder.path("/mantenimiento/paginado")
						.queryParam("page", page).queryParam("size", size).build())
				.retrieve()
				.body(new ParameterizedTypeReference<PaginaResponse<MantenimientoManualResponseDTO>>() {});
	}

	@Override
	public MantenimientoManualResponseDTO obtenerDetalle(Integer id) {
		return clienteWeb.get().uri("/mantenimiento/{id}", id).retrieve()
				.body(MantenimientoManualResponseDTO.class);
	}

	@Override
	public List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId) {
		return clienteWeb.get().uri("/mantenimiento/historial/{equipoId}", equipoId).retrieve()
				.body(new ParameterizedTypeReference<List<MantenimientoManualResponseDTO>>() {
				});
	}

	@Override
	public MantenimientoManualResponseDTO cerrar(Integer id) {
		return clienteWeb.post().uri("/mantenimiento/cerrar/{id}", id).retrieve()
				.body(MantenimientoManualResponseDTO.class);
	}

	@Override
	public byte[] descargarPdf(Integer id) {
		try {
			return clienteWeb.get().uri("/mantenimiento/{id}/pdf", id).retrieve().body(byte[].class);
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public byte[] obtenerImagen(Integer idMantenimiento, String filename) {
		try {
			return clienteWeb.get()
					.uri("/mantenimiento/{id}/imagenes/{filename}", idMantenimiento, filename)
					.retrieve()
					.body(byte[].class);
		} catch (RestClientResponseException ex) {
			if (ex.getStatusCode().value() == 404) {
				return null;
			}
			throw WebClientHelper.manejarError(ex);
		}
	}

	@Override
	public void reenviarCorreo(Integer id) {
		try {
			clienteWeb.post().uri("/mantenimiento/{id}/reenviar-correo", id).retrieve().toBodilessEntity();
		} catch (RestClientResponseException ex) {
			throw WebClientHelper.manejarError(ex);
		}
	}
}
