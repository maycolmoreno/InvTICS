package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.UsuariosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UsuariosResponseDTO;

public interface IUsuariosServicio {
	public List<UsuariosResponseDTO> listarUsuario();
	public void nuevoUsuario(UsuariosRequestDTO dto);
	UsuariosResponseDTO obtenerUsuario(Integer id);
	void actualizarUsuario(Integer id, UsuariosRequestDTO dto);
	void eliminarUsuario(Integer id);
}
