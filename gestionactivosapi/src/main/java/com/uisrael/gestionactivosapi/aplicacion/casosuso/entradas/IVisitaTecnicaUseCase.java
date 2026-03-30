package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.CustodioVisita;
import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;

public interface IVisitaTecnicaUseCase {

    List<EquipoVisita> obtenerEquipos(Long ubicacionId, Long custodioId);

    List<CustodioVisita> obtenerCustodios(Long ubicacionId);
}
