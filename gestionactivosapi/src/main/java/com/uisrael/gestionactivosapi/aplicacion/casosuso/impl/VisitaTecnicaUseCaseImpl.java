package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.CustodioVisita;
import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoVisitaRepositorioPuerto;

public class VisitaTecnicaUseCaseImpl implements IVisitaTecnicaUseCase {

    private final EquipoVisitaRepositorioPuerto equipoVisitaRepositorio;

    public VisitaTecnicaUseCaseImpl(EquipoVisitaRepositorioPuerto equipoVisitaRepositorio) {
        this.equipoVisitaRepositorio = equipoVisitaRepositorio;
    }

    @Override
    public List<EquipoVisita> obtenerEquipos(Long ubicacionId, Long custodioId) {
        List<EquipoVisita> equipos = equipoVisitaRepositorio.findEquiposByUbicacionAndCustodio(ubicacionId, custodioId);
        return equipos != null ? equipos : List.of();
    }

    @Override
    public List<CustodioVisita> obtenerCustodios(Long ubicacionId) {
        List<EquipoVisita> equipos = equipoVisitaRepositorio.findEquiposByUbicacionAndCustodio(ubicacionId, null);
        if (equipos == null || equipos.isEmpty()) {
            return List.of();
        }

        Map<Integer, CustodioVisita> unicos = equipos.stream()
                .filter(e -> e.getIdCustodio() > 0)
                .map(e -> new CustodioVisita(
                        e.getIdCustodio(),
                        e.getCustodioNombre(),
                        e.getCustodioArea() != null ? e.getCustodioArea() : ""))
                .collect(Collectors.toMap(
                        CustodioVisita::idCustodio,
                        cv -> cv,
                        (a, b) -> a));

        return unicos.values().stream()
                .sorted(Comparator.comparing(
                        cv -> cv.nombre() != null ? cv.nombre() : "",
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
