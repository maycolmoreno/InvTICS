package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMantenimientosRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMantenimientosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;

public class MantenimientosRepositorioImpl implements IMantenimientosRepositorio {

    private final IMantenimientosJpaRepositorio jpaRepository;
    private final IMantenimientosJpaMapper entityMapper;

    public MantenimientosRepositorioImpl(IMantenimientosJpaRepositorio jpaRepository,
            IMantenimientosJpaMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Mantenimientos guardar(Mantenimientos mantenimiento) {
        MantenimientosJpa entity = entityMapper.toEntity(mantenimiento);
        MantenimientosJpa guardado = jpaRepository.save(entity);
        return entityMapper.toDomain(guardado);
    }

    @Override
    public List<Mantenimientos> listarTodos() {
        return jpaRepository.findAllByOrderByFechaProgramadaDescIdMantenimientoDesc()
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Mantenimientos> buscarPorId(int id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }
}
