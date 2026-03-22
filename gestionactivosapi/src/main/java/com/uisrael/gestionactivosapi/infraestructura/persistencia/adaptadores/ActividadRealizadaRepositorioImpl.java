package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.ActividadRealizada;
import com.uisrael.gestionactivosapi.dominio.repositorios.IActividadRealizadaRepository;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadRealizadaJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;

public class ActividadRealizadaRepositorioImpl implements IActividadRealizadaRepository {

    private final IActividadRealizadaJpaRepositorio jpaRepositorio;

    public ActividadRealizadaRepositorioImpl(IActividadRealizadaJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public void eliminarPorMantenimiento(int idMantenimiento) {
        jpaRepositorio.deleteByIdMantenimiento(idMantenimiento);
    }

    @Override
    public List<ActividadRealizada> guardarTodas(List<ActividadRealizada> actividades) {
        List<ActividadRealizadaJpa> entities = actividades.stream().map(this::toEntity).toList();
        return jpaRepositorio.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ActividadRealizada> listarPorMantenimiento(int idMantenimiento) {
        return jpaRepositorio.findAllByIdMantenimiento(idMantenimiento).stream().map(this::toDomain).toList();
    }

    private ActividadRealizadaJpa toEntity(ActividadRealizada d) {
        ActividadRealizadaJpa e = new ActividadRealizadaJpa();
        e.setIdActividadRealizada(d.getIdActividadRealizada());
        e.setIdMantenimiento(d.getIdMantenimiento());
        e.setIdActividad(d.getIdActividad());
        e.setRealizada(d.isRealizada());
        return e;
    }

    private ActividadRealizada toDomain(ActividadRealizadaJpa e) {
        ActividadRealizada d = new ActividadRealizada();
        d.setIdActividadRealizada(e.getIdActividadRealizada());
        d.setIdMantenimiento(e.getIdMantenimiento());
        d.setIdActividad(e.getIdActividad());
        d.setRealizada(Boolean.TRUE.equals(e.getRealizada()));
        return d;
    }
}
