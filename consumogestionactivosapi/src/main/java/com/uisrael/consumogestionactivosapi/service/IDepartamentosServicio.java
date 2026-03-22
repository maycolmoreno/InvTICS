package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.DepartamentosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.DepartamentosResponseDTO;

public interface IDepartamentosServicio {

	public List<DepartamentosResponseDTO> listarDepartamentos();

	public void crearDepartamento(DepartamentosRequestDTO dto);

	DepartamentosResponseDTO obtenerPorId(Integer idDepartamento);

	void actualizarDepartamento(Integer idDepartamento, DepartamentosRequestDTO dto);

	void actualizarEstado(Integer idDepartamento, boolean estado);

	boolean nombreExiste(String nombre);

	boolean nombreExisteParaOtro(String nombre, int idDepartamento);

}
