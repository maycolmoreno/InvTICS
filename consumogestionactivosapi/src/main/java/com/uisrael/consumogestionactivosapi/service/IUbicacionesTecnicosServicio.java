package com.uisrael.consumogestionactivosapi.service;

import java.time.LocalDate;
import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ConsentimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.UbicacionTecnicoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.HistorialGpsResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionActivaResponseDTO;

public interface IUbicacionesTecnicosServicio {

	void registrarConsentimiento(ConsentimientoRequestDTO dto);

	void enviarUbicacion(UbicacionTecnicoRequestDTO dto);

	List<UbicacionActivaResponseDTO> obtenerUbicacionesTiempoReal();

	List<HistorialGpsResponseDTO> obtenerHistorialGps(LocalDate fecha);
}
