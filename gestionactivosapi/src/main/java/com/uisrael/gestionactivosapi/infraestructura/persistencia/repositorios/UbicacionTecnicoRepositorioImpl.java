package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionTecnicoJpaRepositorio;

public class UbicacionTecnicoRepositorioImpl implements UbicacionTecnicoPort {

    private final IUbicacionTecnicoJpaRepositorio jpaRepositorio;

    public UbicacionTecnicoRepositorioImpl(IUbicacionTecnicoJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public UbicacionTecnicoJpa guardar(UbicacionTecnicoJpa ubicacion) {
        return jpaRepositorio.save(ubicacion);
    }

    @Override
    public List<UbicacionTecnicoJpa> ultimasPorTecnicosActivos(LocalDateTime desde) {
        return jpaRepositorio.findUltimaUbicacionPorTecnicosActivos(desde);
    }

    @Override
    public List<UbicacionTecnicoJpa> historialPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return jpaRepositorio.findHistorialPorFecha(desde, hasta);
    }

    @Override
    @Transactional
    public int eliminarAnterioresA(LocalDateTime fecha) {
        return jpaRepositorio.deleteByTimestampCapturaBefore(fecha);
    }
}
