package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;

public interface EquipoVisitaRepositorioPuerto {

    List<EquipoVisita> findEquiposByUbicacionAndCustodio(Long ubicacionId, Long custodioId);
}
