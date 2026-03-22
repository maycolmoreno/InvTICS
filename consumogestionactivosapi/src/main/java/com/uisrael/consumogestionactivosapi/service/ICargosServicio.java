package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CargosResponseDTO;

public interface ICargosServicio {

	public List<CargosResponseDTO> listarCargos();

	public void crearCargo(CargosRequestDTO dto);

	CargosResponseDTO obtenerPorId(Integer idCargo);

	void actualizarCargo(Integer idCargo, CargosRequestDTO dto);

	void actualizarEstado(Integer idCargo, boolean estado);

	boolean nombreExiste(String nombre);

	boolean nombreExisteParaOtro(String nombre, int idCargo);
}
