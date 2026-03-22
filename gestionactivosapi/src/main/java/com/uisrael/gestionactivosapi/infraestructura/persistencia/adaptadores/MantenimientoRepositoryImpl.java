package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMantenimientoRepository;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMantenimientosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;

public class MantenimientoRepositoryImpl implements IMantenimientoRepository {

    private final IMantenimientosJpaRepositorio jpaRepository;
    private final IMantenimientosJpaMapper mapper;

    public MantenimientoRepositoryImpl(IMantenimientosJpaRepositorio jpaRepository,
            IMantenimientosJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Mantenimientos guardar(Mantenimientos mantenimiento) {
        MantenimientosJpa entity = mapper.toEntity(mantenimiento);
        MantenimientosJpa saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Mantenimientos> guardarTodos(List<Mantenimientos> mantenimientos) {
        List<MantenimientosJpa> entities = mantenimientos.stream().map(mapper::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Mantenimientos> buscarPorId(int id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Integer obtenerMaxSecuenciaPorYear(int yearSnapshoted) {
        String max = jpaRepository.findMaxSineSnapshotedByYear(yearSnapshoted);
        if (max == null || !max.contains("-")) {
            return null;
        }
        String[] parts = max.split("-");
        if (parts.length != 2) {
            return null;
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public LocalDateTime obtenerUltimoCierrePorEquipo(int equipoId) {
        return jpaRepository.findTopByEquipoIdAndFecCierreNotNullOrderByFecCierreDesc(equipoId)
                .map(MantenimientosJpa::getFecCierre)
                .orElse(null);
    }
}
