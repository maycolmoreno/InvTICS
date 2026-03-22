package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MarcasResponseDTO;

public interface IMarcasServicio {
	//Listar
	public List<MarcasResponseDTO> listarMarca();

	//Nuevo
	public void nuevaMarca(MarcasRequestDTO dto);

	//Obtener
	  MarcasResponseDTO obtenerMarca(Integer id);

	//Editar
	void actualizarMarca(Integer id, MarcasRequestDTO dto);

	//Eliminar
	 void eliminarMarca(Integer id);



}
