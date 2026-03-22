package com.uisrael.consumogestionactivosapi.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MarcasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

@Service
public class MarcasServicioImpl  implements IMarcasServicio{

	private final WebClient clienteweb;

	public MarcasServicioImpl(WebClient clienteweb) {
		super();
		this.clienteweb = clienteweb;
	}

	@Override
	public List<MarcasResponseDTO> listarMarca() {
		return clienteweb.get().uri("/marcas").retrieve().bodyToFlux(MarcasResponseDTO.class).collectList().block();
	}

	 @Override
	    public void nuevaMarca(MarcasRequestDTO dto) {

	        // ✅ VALIDACIÓN LOCAL (ignora mayúsculas/minúsculas)
	        String nombreNuevo = normalizarTexto(dto.getNombre());
	        List<MarcasResponseDTO> existentes = listarMarca();

	        boolean existe = existentes.stream()
	                .anyMatch(m -> normalizarTexto(m.getNombre()).equals(nombreNuevo));

	        if (existe) {
	            throw new IllegalArgumentException("Ya existe una marca con ese nombre");
	        }

	        // ✅ GUARDAR EN API
	        try {
	            clienteweb.post()
	                    .uri("/marcas")
	                    .bodyValue(dto)
	                    .retrieve()
	                    .toBodilessEntity()
	                    .block();

	        } catch (WebClientResponseException e) {

	            // Si la API responde duplicado como 400 o 409
	            if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT) {
	                throw new IllegalArgumentException("Ya existe una marca con ese nombre");
	            }

	            throw e;
	        }
	    }

	 @Override
	    public MarcasResponseDTO obtenerMarca(Integer id) {
	        try {
	            return clienteweb.get()
	                    .uri("/marcas/{id}", id)
	                    .retrieve()
	                    .bodyToMono(MarcasResponseDTO.class)
	                    .block();
	        } catch (WebClientResponseException e) {
	            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
	                return null;
	            }
	            throw e;
	        }
	    }


	 @Override
	    public void actualizarMarca(Integer id, MarcasRequestDTO dto) {

	        int idEditando = id;

	        // ✅ VALIDACIÓN LOCAL (excluye el mismo id)
	        String nombreNuevo = normalizarTexto(dto.getNombre());
	        List<MarcasResponseDTO> existentes = listarMarca();

	        boolean duplicado = existentes.stream()
	                .filter(m -> obtenerIdMarca(m) != idEditando) // ✅ excluye el mismo registro
	                .anyMatch(m -> normalizarTexto(m.getNombre()).equals(nombreNuevo));

	        if (duplicado) {
	            throw new IllegalArgumentException("Ya existe una marca con ese nombre");
	        }

	        // ✅ ACTUALIZAR EN API
	        try {
	            clienteweb.put()
	                    .uri("/marcas/{id}", id)
	                    .bodyValue(dto)
	                    .retrieve()
	                    .toBodilessEntity()
	                    .block();

	        } catch (WebClientResponseException e) {

	            if (e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT) {
	                throw new IllegalArgumentException("Ya existe una marca con ese nombre");
	            }

	            throw e;
	        }
	    }

	@Override
	 public void eliminarMarca(Integer id) {
        clienteweb.delete()
                .uri("/marcas/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

	 private String normalizarTexto(String texto) {
	        return texto == null ? "" : texto.trim().toUpperCase();
	    }


	    private int obtenerIdMarca(MarcasResponseDTO m) {
	        // ✅ AJUSTA si tu getter es distinto:
	        return m.getIdMarca();
	    }


}
