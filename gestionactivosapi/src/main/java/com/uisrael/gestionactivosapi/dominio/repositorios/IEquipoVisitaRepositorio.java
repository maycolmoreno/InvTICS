package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;

public interface IEquipoVisitaRepositorio {

    List<EquipoVisita> findEquiposByUbicacionAndCustodio(Long ubicacionId, Long custodioId);
}
