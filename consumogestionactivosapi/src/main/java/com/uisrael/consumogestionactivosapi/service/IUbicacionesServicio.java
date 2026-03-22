package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;

public interface IUbicacionesServicio {

	public List<UbicacionesResponseDTO> listarUbicaciones();

	public void crearUbicacion(UbicacionesRequestDTO dto);

	UbicacionesResponseDTO obtenerPorId(Integer idUbicacion);

	void actualizarUbicacion(Integer idUbicacion, UbicacionesRequestDTO dto);

	void actualizarEstado(Integer idUbicacion, boolean estado);

	boolean nombreExiste(String nombre);

	boolean nombreExisteParaOtro(String nombre, int idUbicacion);
}
