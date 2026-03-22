package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.RolesRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.RolesResponseDTO;

public interface IRolesServicio {
	public List<RolesResponseDTO> listarRol();
	public void nuevoRol(RolesRequestDTO dto);
	RolesResponseDTO obtenerRol(Integer id);
	void actualizarRol(Integer id, RolesRequestDTO dto);
	void eliminarRol(Integer id);
}
