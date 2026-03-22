package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaCustodioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.VisitaEquipoResponseDTO;

public interface IVisitaTecnicaUseCase {

    List<VisitaEquipoResponseDTO> obtenerEquipos(Long ubicacionId, Long custodioId);

    List<VisitaCustodioResponseDTO> obtenerCustodios(Long ubicacionId);
}
