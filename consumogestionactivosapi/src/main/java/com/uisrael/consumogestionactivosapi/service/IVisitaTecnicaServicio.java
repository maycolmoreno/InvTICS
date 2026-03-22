package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.VisitaEquipoResponseDTO;

public interface IVisitaTecnicaServicio {

    List<VisitaEquipoResponseDTO> obtenerEquipos(Long ubicacionId, Long custodioId);

    List<VisitaCustodioResponseDTO> obtenerCustodios(Long ubicacionId);
}
