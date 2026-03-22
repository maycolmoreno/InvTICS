package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CategoriaEquiposResponseDTO;

public interface ICategoriaEquiposServicio {
	public List<CategoriaEquiposResponseDTO> listarCategoriaEquipo();
	public void nuevoCategoriaEquipo(CategoriaEquiposRequestDTO dto);
	CategoriaEquiposResponseDTO obtenerCategoriaEquipo(Integer id);
	void actualizarCategoriaEquipo(Integer id, CategoriaEquiposRequestDTO dto);
	void eliminarCategoriaEquipo(Integer id);
}
