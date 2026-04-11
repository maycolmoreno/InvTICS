package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ModuloResponseDTO;

public interface IModulosServicio {

	List<ModuloResponseDTO> listarModulos();

	List<ModuloResponseDTO> listarModulosPorRol(Integer rolId);

	void actualizarModulosRol(Integer rolId, List<Integer> moduloIds);
}
